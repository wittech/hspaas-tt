package com.huashi.mms.record.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.PropertyPreFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.huashi.common.user.service.IUserService;
import com.huashi.mms.config.cache.redis.constant.MmsRedisConstant;
import com.huashi.mms.config.worker.fork.MtReportPushToDeveloperWorker;
import com.huashi.mms.record.dao.MmsMtMessagePushMapper;
import com.huashi.mms.record.domain.MmsMtMessageDeliver;
import com.huashi.mms.record.domain.MmsMtMessagePush;
import com.huashi.mms.record.domain.MmsMtMessageSubmit;
import com.huashi.sms.passage.context.PassageContext.DeliverStatus;
import com.huashi.sms.passage.context.PassageContext.PushStatus;
import com.huashi.util.HttpClientUtil;
import com.huashi.util.HttpClientUtil.RetryResponse;

/**
 * TODO 下行彩信推送服务实现
 * 
 * @author zhengying
 * @version V1.0
 * @date 2019年1月15日 下午5:59:30
 */
@Service
public class MmsMtPushService implements IMmsMtPushService {

    @Reference(mock = "fail:return+null")
    private IUserService           userService;
    @Autowired
    private MmsMtMessagePushMapper mmsMtMessagePushMapper;
    @Resource
    private StringRedisTemplate    stringRedisTemplate;
    @Autowired
    private IMmsMtSubmitService    mmsMtSubmitService;

    @Value("${thread.poolsize.push:2}")
    private int                    pushThreadPoolSize;
    @Autowired
    private ApplicationContext     applicationContext;

    private final Logger           logger                   = LoggerFactory.getLogger(getClass());

    /**
     * 推送分包分割主键（根据推送地址进行切割）
     */
    private static final String    PUSH_BODY_SUBPACKAGE_KEY = "pushUrl";

    @Override
    public void savePushMessage(List<MmsMtMessagePush> pushes) {
        long start = System.currentTimeMillis();
        mmsMtMessagePushMapper.batchInsert(pushes);
        logger.info("推送数据插入耗时：{} ms", (System.currentTimeMillis() - start));
    }

    @Override
    public boolean doListenerAllUser() {
        Set<Integer> userIds = userService.findAvaiableUserIds();
        if (CollectionUtils.isEmpty(userIds)) {
            logger.error("待推送可用用户数据为空，无法监听");
            return false;
        }

        try {
            for (Integer userId : userIds) {
                addUserMtPushListener(userId);
            }

            return true;
        } catch (Exception e) {
            logger.info("用户初始化下行推送队列失败", e);
        }

        return false;
    }

    /**
     * 获取当前用户[userId]对应的状态报告推送队列名称
     * 
     * @param userId 用户ID，每个用户ID不同的队列（用户独享队列，非共享一个推送队列）
     * @return
     */
    @Override
    public String getUserPushQueueName(Integer userId) {
        return String.format("%s:%d", MmsRedisConstant.RED_QUEUE_MMS_MT_WAIT_PUSH, userId);
    }

    /**
     * TODO 组装报文前半部分信息
     * 
     * @param body 报文信息（上下文使用）
     * @param deliver 网关回执数据
     * @param cachedPushArgs 本地缓存推送参数信息（为了加快速度缓存变量，而不是每次都根据MSG_ID和MOBILE查询REDIS或者DB）
     * @param failoverDeliversQueue 本次处理失败（一般是查询REDIS或者DB都没有推送配置信息）重入待处理回执状态（后续线程重试）
     * @return
     */
    private boolean assembleBody(JSONObject body, MmsMtMessageDeliver deliver, Map<String, JSONObject> cachedPushArgs,
                                 List<MmsMtMessageDeliver> failoverDeliversQueue) {
        // 如果本地缓存中存在相关值，则直接获取，无需请求REDIS或DB
        if (MapUtils.isNotEmpty(cachedPushArgs) && cachedPushArgs.containsKey(deliver.getMsgId())) {
            body.putAll(cachedPushArgs.get(deliver.getMsgId()));
            return true;
        }

        else {
            try {
                JSONObject redisArgs = getWaitPushBodyArgs(deliver.getMsgId(), deliver.getMobile());
                if (redisArgs == null) {
                    // 如果数据生成时间超过[5分钟]舍弃，不再重新补偿
                    // if(System.currentTimeMillis() - deliver.getCreateTime().getTime() >= 5 * 60 * 1000 )

                    if (System.currentTimeMillis() - deliver.getCreateTime().getTime() >= 20 * 1000) {
                        return false;
                    }

                    failoverDeliversQueue.add(deliver);
                } else {
                    body.putAll(redisArgs);
                    cachedPushArgs.put(deliver.getMsgId(), body);
                    return true;

                }
            } catch (IllegalStateException e) {
                logger.warn(e.getMessage());
            }

            return false;
        }
    }

    /**
     * TODO 回执信息按照用户分包，上家一次性回执多个回执报文，报文中很可能是我司多个用户数据回执信息，顾要分包
     * 
     * @param body 单个报文信息
     * @param deliver 上家回执报文信息
     * @param userBodies 本次需要处理的过程累计的报文集合信息
     * @return
     */
    private boolean subQueue(JSONObject body, MmsMtMessageDeliver deliver, Map<Integer, List<JSONObject>> userBodies) {
        // edit by zhengying 将SID转型为string（部分客户提到推送到客户侧均按照字符类型去解析，顾做了转义）
        body.put("sid", body.getLong("sid").toString());
        body.put("mobile", deliver.getMobile());
        body.put("status", deliver.getStatusCode());
        body.put("receiveTime", deliver.getDeliverTime());
        body.put("errorMsg", deliver.getStatus() == DeliverStatus.SUCCESS.getValue() ? "" : deliver.getStatusCode());

        try {
            // 如果本次处理的用户ID已经包含在上下文处理集合中，则直接追加即可
            if (MapUtils.isNotEmpty(userBodies) && userBodies.containsKey(body.getInteger("userId"))) {
                userBodies.get(body.getInteger("userId")).add(body);
            } else {
                // 如果未曾处理过，则重新初始化集合
                // List<JSONObject> ds = new ArrayList<>();
                // ds.add(body);
                userBodies.put(body.getInteger("userId"), new ArrayList<>(Arrays.asList(body)));
            }

            return true;
        } catch (Exception e) {
            logger.error("解析推送数据报告异常:{}", body.toJSONString(), e);
            return false;
        }
    }

    /**
     * 比较报文内容并完成加入推送redis队列（方法异步）
     * 
     * @param delivers
     * @return
     * @see com.huashi.sms.record.service.ISmsMtPushService#compareAndPushBody(java.util.List)
     */
    @Override
    // @Async
    public Future<Boolean> compareAndPushBody(List<MmsMtMessageDeliver> delivers) {
        if (CollectionUtils.isEmpty(delivers)) {
            return new AsyncResult<Boolean>(false);
        }

        Map<String, JSONObject> cachedPushArgs = new HashMap<>();
        // 用户ID对应的 推送报告集合数据
        Map<Integer, List<JSONObject>> userBodies = new HashMap<>();
        // 针对上家回执数据已回，但我方回执数据未入库以及REDIS没有推送配置信息，后续线程重试补完成
        List<MmsMtMessageDeliver> failoverDeliversQueue = new ArrayList<>();

        try {
            for (MmsMtMessageDeliver deliver : delivers) {
                if (deliver == null) {
                    continue;
                }

                JSONObject body = new JSONObject();
                if (!assembleBody(body, deliver, cachedPushArgs, failoverDeliversQueue)) {
                    continue;
                }

                removeReadyMtPushConfig(deliver.getMsgId(), deliver.getMobile());

                // 如果用户推送地址为空则表明不需要推送
                if (StringUtils.isEmpty(body.getString(PUSH_BODY_SUBPACKAGE_KEY))) {
                    continue;
                }

                if (!subQueue(body, deliver, userBodies)) {
                    continue;
                }
            }

            // 如果针对上家已回执我方未入库数据存在则保存至REDIS
            if (CollectionUtils.isNotEmpty(failoverDeliversQueue)) {
                sendToDeliverdFailoverQueue(failoverDeliversQueue);
            }

            // 根据用户ID分别组装数据，并发送至各自队列, key:userId, value:bodies（推送报文数据）
            for (Entry<Integer, List<JSONObject>> userBody : userBodies.entrySet()) {
                stringRedisTemplate.opsForList().rightPush(getUserPushQueueName(userBody.getKey()),
                                                           JSON.toJSONString(userBody.getValue()));
            }
            return new AsyncResult<Boolean>(true);
        } catch (Exception e) {
            logger.error("将上家回执数据发送至待推送队列逻辑失败，回执数据为：{}", JSON.toJSONString(delivers), e);
        }

        // 处理本次资源，加速GC
        userBodies = null;
        cachedPushArgs = null;
        failoverDeliversQueue = null;

        return new AsyncResult<Boolean>(false);
    }

    /**
     * TODO 针对上家回执数据已回但我方回执数据未入库情况需要 推送集合数据
     * 
     * @param failoverDeliversQueue
     */
    private void sendToDeliverdFailoverQueue(List<MmsMtMessageDeliver> failoverDeliversQueue) {
        try {
            // 目前数据的超时时间按照 创建是时间超过5分钟则超时
            stringRedisTemplate.opsForList().rightPush(MmsRedisConstant.RED_QUEUE_MMS_DELIVER_FAILOVER,
                                                       JSON.toJSONString(failoverDeliversQueue,
                                                                         new SimplePropertyPreFilter("msgId", "mobile",
                                                                                                     "statusCode",
                                                                                                     "deliverTime",
                                                                                                     "remark",
                                                                                                     "status",
                                                                                                     "createTime")));
            // stringRedisTemplate.expire(SmsRedisConstant.RED_MESSAGE_DELIVED_WAIT_PUSH_LIST, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            logger.error("针对上家回执数据已回但我方回执数据未入库情况需要 推送集合数据失败", e);
        }
    }

    /**
     * TODO 获取待处理的推送报告定义参数信息 eg. （SID, pushUrl, pushTimes ...）
     * 
     * @param msgId
     * @param mobile
     * @return
     */
    @Override
    public JSONObject getWaitPushBodyArgs(String msgId, String mobile) {
        // 首先在REDIS查询是否存在数据
        try {
            HashOperations<String, String, String> hashOperations = stringRedisTemplate.opsForHash();
            if (hashOperations.hasKey(getMtPushConfigKey(msgId), mobile)) {
                Object o = hashOperations.get(getMtPushConfigKey(msgId), mobile);
                if (o != null) {
                    return JSON.parseObject(o.toString());
                }

            }
        } catch (Exception e) {
            logger.warn("回执完成逻辑中获取待推送设置数据REDIS异常，DB补偿, {}", e.getMessage());
        }

        // 如果REDIS没有或者REDIS 异常，需要查询DB是否有数据（REDIS过期后自动释放，顾要做兼容判断）
        return getUserPushConfigFromDatabase(msgId, mobile);
    }

    /**
     * TODO REDIS 查询不到反查数据库是否需要推送
     * 
     * @param msgId
     * @param mobile
     * @return
     */
    private JSONObject getUserPushConfigFromDatabase(String msgId, String mobile) {
        if (StringUtils.isEmpty(msgId) || StringUtils.isEmpty(mobile)) {
            return null;
        }

        MmsMtMessagePush push = mmsMtMessagePushMapper.findByMobileAndMsgid(mobile, msgId);
        if (push != null) {
            throw new IllegalStateException("msgId:" + msgId + ", mobile:" + mobile + "推送记录已存在，忽略");
        }

        // 此处需要查询数据库是否需要有推送设置，无则不推送
        MmsMtMessageSubmit submit = mmsMtSubmitService.getByMsgidAndMobile(msgId, mobile);
        if (submit == null) {
            // logger.warn("msg_id : {}, mobile: {} 未找到短信相关提交数据", msgId, mobile);
            return null;
        }

        JSONObject pushSettings = new JSONObject();
        pushSettings.put("sid", submit.getSid());
        pushSettings.put("userId", submit.getUserId());
        pushSettings.put("msgId", msgId);
        pushSettings.put("attach", submit.getAttach());
        pushSettings.put("pushUrl", submit.getPushUrl());
        pushSettings.put("retryTimes", HttpClientUtil.PUSH_RETRY_TIMES);

        return pushSettings;
    }

    /**
     * TODO
     * 
     * @param msgId
     * @return
     */
    @Override
    public String getMtPushConfigKey(String msgId) {
        return String.format("%s:%s", MmsRedisConstant.RED_READY_MT_PUSH_CONFIG, msgId);
    }

    /**
     * TODO 移除待推送信息配置信息
     * 
     * @param msgId
     * @param mobile
     */
    private void removeReadyMtPushConfig(String msgId, String mobile) {
        try {
            stringRedisTemplate.opsForHash().delete(getMtPushConfigKey(msgId), mobile);
        } catch (Exception e) {
            logger.error("移除待推送消息参数设置失败, msg_id : {}", msgId, e);
        }
    }

    @Override
    @Async
    public void setMessageReadyPushConfigurations(List<MmsMtMessageSubmit> submits) {
        try {
            for (MmsMtMessageSubmit submit : submits) {
                stringRedisTemplate.opsForHash().put(getMtPushConfigKey(submit.getMsgId()),
                                                     submit.getMobile(),
                                                     JSON.toJSONString(submit,
                                                                       new SimplePropertyPreFilter("sid", "userId",
                                                                                                   "msgId", "attach",
                                                                                                   "pushUrl",
                                                                                                   "retryTimes")));
                stringRedisTemplate.expire(getMtPushConfigKey(submit.getMsgId()), 3, TimeUnit.HOURS);
            }

        } catch (Exception e) {
            logger.error("设置待推送配置消息: {} 失败", JSON.toJSONString(submits), e);
        }
    }

    /**
     * TODO 推送守候线程名称
     * 
     * @param userId 用户ID
     * @param sequence 序列号
     * @return
     */
    private static String pushThreadName(Integer userId, Integer sequence) {
        return String.format("push-daemon-thread-%d-%d", userId, sequence == null ? 1 : sequence++);
    }

    @Override
    public boolean addUserMtPushListener(Integer userId) {
        try {
            for (int i = 0; i < pushThreadPoolSize; i++) {
                Thread thread = new Thread(new MtReportPushToDeveloperWorker(applicationContext,
                                                                             getUserPushQueueName(userId)),
                                           pushThreadName(userId, i));
                thread.start();
            }

            logger.info("用户[" + userId + "]推送队列[" + getUserPushQueueName(userId) + "]开始监听..");
            return true;
        } catch (Exception e) {
            logger.error("用户加入彩信状态报告回执推送监听失败, 用户ID：{}", userId, e);
        }
        return false;
    }

    @Override
    public void pushMessageBodyToDeveloper(List<JSONObject> bodies) {
        // 资源URL对应的推送地址(资源地址对应的总量超过500应该分包发送)
        Map<String, List<JSONObject>> urlBodies = new HashMap<>();
        String urlKey = null;

        for (JSONObject body : bodies) {
            if (MapUtils.isEmpty(body)) {
                continue;
            }

            if (StringUtils.isEmpty(body.getString(PUSH_BODY_SUBPACKAGE_KEY))) {
                continue;
            }

            urlKey = body.getString(PUSH_BODY_SUBPACKAGE_KEY);

            // 根据用户的推送'URL'进行拆分组装状态报告
            try {
                if (MapUtils.isNotEmpty(urlBodies) && urlBodies.containsKey(urlKey)) {
                    urlBodies.get(urlKey).add(body);
                } else {
                    urlBodies.put(urlKey, new ArrayList<>(Arrays.asList(body)));
                }
            } catch (Exception e) {
                logger.error("解析推送数据报文异常:{}", body.toJSONString(), e);
                continue;
            }
        }

        sendBody(urlBodies);

        urlKey = null;
        urlBodies = null;
    }

    /**
     * 用户推送报告过滤器
     */
    private static final PropertyPreFilter USER_PUSH_REPORT_FILTER = new SimplePropertyPreFilter("sid", "mobile",
                                                                                                 "attach", "status",
                                                                                                 "receiveTime",
                                                                                                 "errorMsg");

    /**
     * TODO 转义用户推送报告数据
     * 
     * @param bodies
     * @return
     */
    private String translateBodies(List<JSONObject> bodies) {
        if (CollectionUtils.isEmpty(bodies)) {
            logger.error("推送报告数据为空");
            return null;
        }

        return JSON.toJSONString(bodies, USER_PUSH_REPORT_FILTER, SerializerFeature.WriteMapNullValue,
                                 SerializerFeature.WriteNullStringAsEmpty);
    }

    /**
     * TODO 发送短信状态报文至下家并完成异步持久化
     * 
     * @param urlBodies
     * @return
     */
    private boolean sendBody(Map<String, List<JSONObject>> urlBodies) {
        try {
            for (Entry<String, List<JSONObject>> urlBody : urlBodies.entrySet()) {
                doPushPersistence(urlBody.getValue(),
                                  HttpClientUtil.postBody(urlBody.getKey(), translateBodies(urlBody.getValue()), 1));
            }

            return true;
        } catch (Exception e) {
            logger.error("推送下家状态报文或持久化失败 ", e);
            return false;
        }
    }

    /**
     * TODO 推送报告持久化
     * 
     * @param report
     * @param retryResponse 推送处理结果
     * @param timeCost 耗时（毫秒）
     */
    private void doPushPersistence(List<JSONObject> bodies, RetryResponse retryResponse) {
        MmsMtMessagePush push = null;
        Set<String> waitPushMsgIdRedisKeys = new HashSet<>();
        List<MmsMtMessagePush> persistPushesList = new ArrayList<>();
        for (JSONObject body : bodies) {
            push = new MmsMtMessagePush();
            push.setMsgId(body.getString("msgId"));
            push.setMobile(body.getString("mobile"));
            if (retryResponse == null) {
                push.setStatus(PushStatus.FAILED.getValue());
                push.setRetryTimes(0);
            } else {
                push.setStatus(retryResponse.isSuccess() ? PushStatus.SUCCESS.getValue() : PushStatus.FAILED.getValue());
                push.setRetryTimes(retryResponse.getAttemptTimes());
            }

            // 暂时先用作批量处理ID
            push.setResponseContent(System.nanoTime() + "");
            push.setResponseMilliseconds(retryResponse.getTimeCost());
            push.setContent(JSON.toJSONString(body, USER_PUSH_REPORT_FILTER, SerializerFeature.WriteMapNullValue,
                                              SerializerFeature.WriteNullStringAsEmpty));
            push.setCreateTime(new Date());

            waitPushMsgIdRedisKeys.add(getMtPushConfigKey(body.getString("msgId")));
            persistPushesList.add(push);
        }

        // 删除待推送消息信息
        stringRedisTemplate.delete(waitPushMsgIdRedisKeys);
        // 发送数据至带持久队列中
        savePushMessage(persistPushesList);

        push = null;
        persistPushesList = null;
        waitPushMsgIdRedisKeys = null;
    }

}
