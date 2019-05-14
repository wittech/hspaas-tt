package com.huashi.sms.config.worker;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;

import redis.clients.jedis.Jedis;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.huashi.sms.config.cache.redis.RedisConfiguration;
import com.huashi.sms.config.worker.hook.ShutdownHookWorker;

/**
 * 抽象进程基础类
 *
 * @author zhengying
 * @version V1.0
 * @date 2017年11月30日 上午9:59:26
 */
public abstract class AbstractWorker<E> implements Runnable {

    protected ApplicationContext applicationContext;

    protected final Logger       logger                        = LoggerFactory.getLogger(getClass());

    /**
     * 批量扫描大小
     */
    private static final int     DEFAULT_SCAN_SIZE             = 2000;

    /**
     * 超时时间毫秒值(JOB一秒内处理)
     */
    private static final int     DEFAULT_TIMEOUT               = 1000;

    /**
     * 当前时间计时
     */
    protected final AtomicLong   timer                         = new AtomicLong(0);

    /**
     * REDIS 队列备份KEY名称前缀
     */
    private static final String  REDIS_QUEUE_BACKUP_KEY_PREFIX = "bak_";

    /**
     * 默认睡眠5毫秒
     */
    private static final int     DEFAULT_SLEEP_TIME            = 5;

    /**
     * 是否终止执行(JVM 应用程序钩子HOOK设置)
     *
     * @return
     */
    protected boolean isApplicationStop() {
        return ShutdownHookWorker.shutdownSignal;
    }

    protected StringRedisTemplate getStringRedisTemplate() {
        return applicationContext.getBean(StringRedisTemplate.class);
    }

    public AbstractWorker(ApplicationContext applicationContext) {
        super();
        this.applicationContext = applicationContext;
    }

    /**
     * 执行具体操作
     *
     * @param list
     */
    protected abstract void operate(List<E> list);

    /**
     * 获取REDIS操作值
     *
     * @return
     */
    protected abstract String redisKey();

    /**
     * 日志便签，方便查看
     *
     * @return
     */
    protected abstract String jobTitle();

    /**
     * redis 备份KEY 名称
     *
     * @return
     */
    protected String redisBackupKey() {
        return REDIS_QUEUE_BACKUP_KEY_PREFIX + redisKey();
    }

    /**
     * 数据消费失败后备份数据，保障数据不丢失
     *
     * @param list 本次失败集合数据
     * @param cause 备份原因
     */
    private void backupIfNecessary(List<E> list, String cause) {
        try {
            Jedis jedis = RedisConfiguration.getJedis();
            jedis.rpush(redisBackupKey(), JSON.toJSONString(list));

            // getStringRedisTemplate().opsForList().rightPushAll(redisBackupKey(), JSON.toJSONString(list));
            logger.info("Queue[" + redisKey() + "]" + " data[" + list.size() + "] backup to queue[" + redisBackupKey()
                        + "] successfully cause by '" + cause + "'");
        } catch (Exception e) {
            logger.error("Queue[" + redisKey() + "]" + " data[" + list.size() + "] backup to queue[" + redisBackupKey()
                         + "] failed cause by '" + cause + "'", e);
        }
    }

    /**
     * 每次扫描的总数量
     *
     * @return
     */
    protected int scanSize() {
        return DEFAULT_SCAN_SIZE;
    }

    /**
     * 截止超时时间（单位：毫秒）
     *
     * @return
     */
    protected long timeout() {
        return DEFAULT_TIMEOUT;
    }

    /**
     * 清除资源，重新开始
     *
     * @param list
     */
    protected void clear(List<E> list) {
        timer.set(0);
        list.clear();
    }

    /**
     * 检查并恢复处理失败或者JVM关闭备份数据（入队列前面，优先处理）
     */
    private void recoverFromBackups() {
        long startTime = System.currentTimeMillis();
        try {
            final List<String> list = new ArrayList<>();
            while (true) {
                String row = getStringRedisTemplate().opsForList().leftPop(redisBackupKey());
                if (StringUtils.isEmpty(row)) {
                    break;
                }

                list.add(row);
            }

            if (CollectionUtils.isEmpty(list)) {
                return;
            }

            // 采用LEFT 入队，优先处理
            getStringRedisTemplate().opsForList().leftPushAll(redisKey(), list);
            logger.info("Recover data[" + list.size() + "] from queue[" + redisBackupKey()
                        + "] successfully, it costs {} ms", (System.currentTimeMillis() - startTime));
        } catch (Exception e) {
            logger.error("Recover data from queue[" + redisBackupKey() + "] failed, it costs {} ms",
                         (System.currentTimeMillis() - startTime), e);
        }

    }

    /**
     * 获取对象实例
     *
     * @param clazz
     * @return
     */
    protected <T> T getInstance(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    @SuppressWarnings("unchecked")
    private Class<E> getClildType() {
        Class<E> clazz = (Class<E>) getClass();
        ParameterizedType type = (ParameterizedType) clazz.getGenericSuperclass();
        Type[] types = type.getActualTypeArguments();
        return (Class<E>) types[0];
    }

    /**
     * 执行任务并统计耗时
     *
     * @param list
     */
    private void executeWithTimeCost(List<E> list) {
        long startTime = System.currentTimeMillis();
        try {

            if (isApplicationStop()) {
                backupIfNecessary(list, "jvm shutdown in doing");
                return;
            }

            operate(list);

            long timeCost = System.currentTimeMillis() - startTime;
            if (timeCost > 500 || list.size() > 50) {
                logger.info("job::[" + jobTitle() + "] 执行耗时：{} ms， 共处理：{} 个", timeCost, list.size());
            }

        } catch (Exception e) {
            logger.error("job::[" + jobTitle() + "] 执行失败", e);
            backupIfNecessary(list, "logic process exception[" + e.getMessage() + "]");
        } finally {
            clear(list);
        }
    }

    /**
     * 休息片刻，防止CPU过高
     */
    private void haveARest() {
        int time = new Random().nextInt(10);
        try {
            Thread.sleep(time == 0 ? DEFAULT_SLEEP_TIME : time);
        } catch (InterruptedException e) {
        }
    }

    /**
     * 时间计数器
     */
    private void timeStarter() {
        // 如果为0则表示初始化状态，则需要至
        if (timer.get() == 0) {
            timer.set(System.currentTimeMillis());
        }
    }

    @Override
    public void run() {

        // 检查备份数据是否有数据产生，如果有则恢复数据
        recoverFromBackups();

        List<E> list = new ArrayList<>();
        while (true) {
            if (isApplicationStop()) {
                if (CollectionUtils.isNotEmpty(list)) {
                    logger.info("Backup data[" + list.size() + "] cause by jvm shutdown.");
                    backupIfNecessary(list, "jvm shutdown");
                }
                break;
            }

            try {

                haveARest();

                // 时间启动器（开始计时）
                timeStarter();

                // 如果本次量达到批量取值数据，则跳出
                if (list.size() >= scanSize()) {
                    executeWithTimeCost(list);
                    continue;
                }

                // 如果本次循环时间超过5秒则跳出
                if (CollectionUtils.isNotEmpty(list) && System.currentTimeMillis() - timer.get() >= timeout()) {
                    executeWithTimeCost(list);
                    continue;
                }

                // 获取REDIS 队列中的数据
                // Object o = getStringRedisTemplate().opsForList().rightPopAndLeftPush(redisKey(), redisBackupKey());
                String row = getStringRedisTemplate().opsForList().leftPop(redisKey());

                // 执行到redis中没有数据为止
                if (StringUtils.isEmpty(row)) {
                    if (CollectionUtils.isNotEmpty(list)) {
                        executeWithTimeCost(list);
                    }

                    continue;
                }

                // 根据值对象的类型进行数据解析，填充
                Object value = JSON.parse(row);
                if (value instanceof List) {
                    list.addAll(JSON.parseArray(row, getClildType()));
                } else {
                    list.add(JSON.parseObject(row, getClildType()));
                }

            } catch (Exception e) {
                logger.error("自定义监听线程过程处理失败，数据为：[" + JSON.toJSONString(list) + "]", e);
            }
        }
    }

}
