/**
 * 
 */
package com.huashi.sms.record.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.huashi.common.settings.domain.PushConfig;
import com.huashi.common.settings.service.IPushConfigService;
import com.huashi.common.settings.service.ISystemConfigService;
import com.huashi.common.user.service.IUserService;
import com.huashi.common.util.DateUtil;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.common.vo.PaginationVo;
import com.huashi.constants.CommonContext.CallbackUrlType;
import com.huashi.sms.config.cache.redis.constant.SmsRedisConstant;
import com.huashi.sms.passage.domain.SmsPassage;
import com.huashi.sms.passage.service.ISmsPassageService;
import com.huashi.sms.record.dao.SmsMoMessagePushMapper;
import com.huashi.sms.record.dao.SmsMoMessageReceiveMapper;
import com.huashi.sms.record.domain.SmsMoMessageReceive;
import com.huashi.sms.record.domain.SmsMtMessageSubmit;
import com.huashi.sms.settings.constant.MobileBlacklistType;
import com.huashi.sms.settings.domain.SmsMobileBlackList;
import com.huashi.sms.settings.service.ISmsMobileBlackListService;
import com.huashi.sms.task.context.MQConstant;

/**
 * 短信接收记录服务接口类
 * 
 * @author Administrator
 *
 */

@Service
public class SmsMoMessageService implements ISmsMoMessageService {

	@Reference
	private IUserService userService;
	@Autowired
	private SmsMoMessageReceiveMapper moMessageReceiveMapper;
	@Autowired
	private ISmsMtSubmitService smsMtSubmitService;
	@Resource
	private StringRedisTemplate stringRedisTemplate;
	@Resource
	private RabbitTemplate rabbitTemplate;
	@Reference
	private IPushConfigService pushConfigService;
	@Reference
    private ISmsPassageService smsPassageService;
	@Autowired
	private SmsMoMessagePushMapper smsMoMessagePushMapper;
	@Reference
	private ISystemConfigService systemConfigService;
	@Autowired
	private ISmsMobileBlackListService smsMobileBlackListService;
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public PaginationVo<SmsMoMessageReceive> findPage(int userId,
			String phoneNumber, String startDate, String endDate,
			String currentPage) {
		if (userId <= 0) {
            return null;
        }

		int _currentPage = PaginationVo.parse(currentPage);

		Map<String, Object> params = new HashMap<>();
		params.put("userId", userId);
		if (StringUtils.isNotEmpty(phoneNumber)) {
			params.put("phoneNumber", phoneNumber);
		}
		params.put("startDate", DateUtil.getStringTurnDate(startDate));
		params.put("endDate", DateUtil.getStringTurnDate(endDate));

		int totalRecord = moMessageReceiveMapper.getCountByUserId(params);
		if (totalRecord == 0) {
            return null;
        }

		params.put("startPage", PaginationVo.getStartPage(_currentPage));
		params.put("pageRecord", PaginationVo.DEFAULT_RECORD_PER_PAGE);

		List<SmsMoMessageReceive> list = moMessageReceiveMapper
				.findPageListByUserId(params);
		if (list == null || list.isEmpty()) {
            return null;
        }

		return new PaginationVo<SmsMoMessageReceive>(list, _currentPage,
				totalRecord);
	}

	@Override
	public BossPaginationVo<SmsMoMessageReceive> findPage(int pageNum,
			String keyword) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("keyword", keyword);
		BossPaginationVo<SmsMoMessageReceive> page = new BossPaginationVo<SmsMoMessageReceive>();
		page.setCurrentPage(pageNum);
		int total = moMessageReceiveMapper.findCount(paramMap);
		if (total <= 0) {
			return page;
		}
		page.setTotalCount(total);
		paramMap.put("start", page.getStartPosition());
		paramMap.put("end", page.getPageSize());
		List<SmsMoMessageReceive> dataList = moMessageReceiveMapper
				.findList(paramMap);
		for (SmsMoMessageReceive record : dataList) {
			if(record.getUserId() != null){
				record.setUserModel(userService.getByUserId(record.getUserId()));
			}
			if(record.getPassageId() != null && record.getPassageId() > 0){
                SmsPassage passage = smsPassageService.findById(record.getPassageId());
                record.setPassageName(passage.getName());
            }
			if(StringUtils.isNotEmpty(record.getMsgId()) && StringUtils.isNotEmpty(record.getMobile())){
				record.setMessagePush(smsMoMessagePushMapper.findByMobileAndMsgid(record.getMobile(), record.getMsgId()));
			}
		}
		page.getList().addAll(dataList);
		return page;
	}

	@Override
	public int doFinishReceive(List<SmsMoMessageReceive> list) {
		int count = 0;
		try {
			for (SmsMoMessageReceive receive : list) {

//				// 31省测试上行自动触发下行功能（测试后上线去掉此逻辑） add by 20170709
//				if(StringUtils.isNotEmpty(receive.getDestnationNo()) && receive.getDestnationNo().startsWith("10691348")) {
//					logger.info("数据已发送短信 {}", JSON.toJSONString(receive));
//					 MessageSendUtil.sms("http://api.hspaas.cn:8080/sms/send", "hsjXxJ2gO75iOK", "f7c3ddd27a61fbe9612b2b9f1e6c8287", 
//							receive.getMobile(), "【惠尔睿智】31省根据上行自动下行");
//				}
				
				// 根据通道ID和消息ID
				SmsMtMessageSubmit submit = smsMtSubmitService.getByMoMapping(receive.getPassageId(), receive.getMsgId(), 
						receive.getMobile(), receive.getDestnationNo());
				if(submit != null) {
					receive.setMsgId(submit.getMsgId());
					receive.setUserId(submit.getUserId());
					receive.setSid(submit.getSid() + "");
					
					// 针对直连协议PassageId反补
					receive.setPassageId(submit.getPassageId());
					
					count++;
					
					// 判断是否包含退订关键词，如果包含直接加入黑名单
					doMobileJoinBlacklist(receive.getMobile(), receive.getContent(), 
							String.format("SID:%d,MSG_ID:%s", submit.getSid(), submit.getMsgId()));
					
					PushConfig pushConfig = pushConfigService.getByUserId(submit.getUserId(), CallbackUrlType.SMS_MO.getCode());
					if(pushConfig == null || StringUtils.isEmpty(pushConfig.getUrl())) {
						receive.setNeedPush(false);
						continue;
					}
					
					receive.setNeedPush(true);
					receive.setPushUrl(pushConfig.getUrl());
					receive.setRetryTimes(pushConfig.getRetryTimes());
					
					sendToPushQueue(receive);
				}
			}
			
			
			// 提交至待DB持久队列
			stringRedisTemplate.opsForList().rightPush(SmsRedisConstant.RED_DB_MESSAGE_MO_RECEIVE_LIST, JSON.toJSONString(list));
			
			return count;
			
		} catch (Exception e) {
			logger.error("处理待回执信息REDIS失败，失败信息：{}", JSON.toJSONString(list), e);
			throw new RuntimeException("短信上行报告失败");
		}
	}
	
	/**
	 * 
	   * TODO 根据上行回执短信内容判断是否需要加入黑名单
	   * 
	   * @param mobile
	   * @param content
	 */
	private void doMobileJoinBlacklist(String mobile, String content, String remark) {
		// 判断回复内容是否包含 黑名单词库内容，如果包括则直接加入黑名单
		String[] words = systemConfigService.getBlacklistWords();
		if(words == null || words.length == 0) {
            words = BLACKLIST_WORDS;
        }
		
		boolean isContains = false;
		for(String wd : words) {
			if(StringUtils.isEmpty(wd)) {
                continue;
            }
			
			if(content.toUpperCase().contains(wd.toUpperCase())) {
				isContains = true;
				break;
			}
		}
		
		if(!isContains) {
            return;
        }
		
		SmsMobileBlackList blacklist = new SmsMobileBlackList();
		blacklist.setMobile(mobile);
		blacklist.setType(MobileBlacklistType.UNSUBSCRIBE.getCode());
		blacklist.setRemark(remark);
		
		smsMobileBlackListService.batchInsert(blacklist);
	}
	
	
	// 默认黑名单词库
	private static final String[] BLACKLIST_WORDS = {"TD", "T", "N"};

	@Override
	public int batchInsert(List<SmsMoMessageReceive> list) {
		if(CollectionUtils.isEmpty(list)) {
            return 0;
        }
		
		return moMessageReceiveMapper.batchInsert(list);
	}

	@Override
	public boolean doReceiveToException(Object obj) {
		try {
			stringRedisTemplate.opsForList().rightPush(SmsRedisConstant.RED_MESSAGE_MO_RECEIPT_EXCEPTION_LIST,
					JSON.toJSONString(obj));
			return true;
		} catch (Exception e) {
			logger.error("上行回执错误信息失败 {}", JSON.toJSON(obj), e);
			return false;
		}
	}
	
	private void sendToPushQueue(SmsMoMessageReceive receive) {
		try {
			// 发送至待推送信息队列
			rabbitTemplate.convertAndSend(MQConstant.EXCHANGE_SMS, MQConstant.MQ_SMS_MO_WAIT_PUSH, receive);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

}
