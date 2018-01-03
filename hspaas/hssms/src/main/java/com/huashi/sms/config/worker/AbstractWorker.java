package com.huashi.sms.config.worker;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.alibaba.fastjson.JSON;
import com.huashi.sms.config.worker.config.SmsDbPersistenceRunner;

/**
 * TODO 抽象进程基础类
 *
 * @author zhengying
 * @version V1.0
 * @date 2017年11月30日 上午9:59:26
 */
public abstract class AbstractWorker<E> implements Runnable {

    protected ApplicationContext applicationContext;

    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 批量扫描大小
     */
    private static final int DEFAULT_SCAN_SIZE = 2000;

    /**
     * 超时时间毫秒值(JOB一秒内处理)
     */
    private static final int DEFAULT_TIMEOUT = 1000;

    /**
     * 当前时间计时
     */
    protected final AtomicLong timer = new AtomicLong(0);

    /**
     * REDIS 队列备份KEY名称前缀
     */
    private static final String REDIS_QUEUE_BACKUP_KEY_PREFIX = "bak_";

    /**
     * TODO 是否终止执行(JVM 应用程序钩子HOOK设置)
     *
     * @return
     */
    protected boolean isStop() {
        return SmsDbPersistenceRunner.isCustomThreadShutdown;
    }

    protected StringRedisTemplate getStringRedisTemplate() {
        return applicationContext.getBean(StringRedisTemplate.class);
    }

    public AbstractWorker(ApplicationContext applicationContext) {
        super();
        this.applicationContext = applicationContext;
    }

    /**
     * TODO 执行具体操作
     *
     * @param list
     */
    protected abstract void operate(List<E> list);

    /**
     * TODO 获取REDIS操作值
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
     * TODO redis 备份KEY 名称
     *
     * @return
     */
    protected String redisBackupKey() {
        return REDIS_QUEUE_BACKUP_KEY_PREFIX + redisKey();
    }

    /**
     * TODO 数据失败后持久化REDIS
     *
     * @param list 本次失败集合数据
     */
    private void backupIfFailed(List<E> list) {
        try {
            getStringRedisTemplate().opsForList().rightPushAll(redisBackupKey(), JSON.toJSONString(list));
            logger.error(jobTitle() + "源数据队列：{} 处理失败，加入失败队列完成：{}，共{}条", redisKey(), redisBackupKey(), list.size());
        } catch (Exception e) {
            logger.error(jobTitle() + "源数据队列：{} 处理失败，加入失败队列异常：{}，共{}条", redisKey(), redisBackupKey(), list.size(), e);
        }
    }

    /**
     * TODO 每次扫描的总数量
     *
     * @return
     */
    protected int scanSize() {
        return DEFAULT_SCAN_SIZE;
    }

    /**
     * TODO 截止超时时间（单位：毫秒）
     *
     * @return
     */
    protected long timeout() {
        return DEFAULT_TIMEOUT;
    }

    /**
     * TODO 清除资源，重新开始
     *
     * @param list
     */
    protected void clear(List<E> list) {
        timer.set(0);
        list.clear();
    }

    /**
     * TODO 获取对象实例
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
        // 3返回实际参数类型(泛型可以写多个)
        Type[] types = type.getActualTypeArguments();
        // 4 获取第一个参数(泛型的具体类) Person.class
        return (Class<E>) types[0];
    }

    /**
     * TODO 执行任务并统计耗时
     *
     * @param list
     */
    private void executeWithTimeCost(List<E> list) {
        long startTime = System.currentTimeMillis();
        long utilTime = startTime - timer.get();
        try {
            operate(list);
            logger.info("job::[" + jobTitle() + "] 执行耗时：{} ms， 共处理：{} 个，距离上次清零时间间隔：{} ms", System.currentTimeMillis() - startTime, list.size(), utilTime);
        } catch (Exception e) {
            logger.error("job::[" + jobTitle() + "] 执行失败", e);
            backupIfFailed(list);
        } finally {
            clear(list);
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
        List<E> list = new ArrayList<>();
        while (true) {
            if (isStop()) {
                logger.info("JVM关闭事件已发起，执行自定义线程池停止...");
                if (CollectionUtils.isNotEmpty(list)) {
                    logger.info("JVM关闭事件---当前线程处理数据不为空，执行最后一次后关闭线程...");
                    executeWithTimeCost(list);
                }

                break;
            }


            try {
                // 先休眠1毫秒，避免cpu占用过高
                Thread.sleep(1L);
            } catch (InterruptedException e) {
                // ignored
            }

            try {

                // 时间启动器（开始计时）
                timeStarter();

                // 如果本次量达到批量取值数据，则跳出
                if (list.size() >= scanSize()) {
                    logger.info("-----------获取size: [" + list.size() + "]");
                    executeWithTimeCost(list);
                    continue;
                }

                // 如果本次循环时间超过5秒则跳出
                if (CollectionUtils.isNotEmpty(list) && System.currentTimeMillis() - timer.get() >= timeout()) {
                    logger.info("-----------由于超时：size: [" + list.size() + "]");
                    executeWithTimeCost(list);
                    continue;
                }

                // 获取REDIS 队列中的数据
//                Object o = getStringRedisTemplate().opsForList().rightPopAndLeftPush(redisKey(), redisBackupKey());
                Object o = getStringRedisTemplate().opsForList().leftPop(redisKey());
                
                // 执行到redis中没有数据为止
                if (o == null) {
                    if (CollectionUtils.isNotEmpty(list)) {
                        logger.info("-----------取完，获取size: [" + list.size() + "]");
                        executeWithTimeCost(list);
                    }

                    continue;
                }

                Object value = JSON.parse(o.toString());
                if (value instanceof List) {
                    list.addAll(JSON.parseArray(o.toString(), getClildType()));
                } else {
                    list.add(JSON.parseObject(o.toString(), getClildType()));
                }

            } catch (Exception e) {
                logger.error("自定义监听线程过程处理失败，数据为：[" + JSON.toJSONString(list) + "]", e);
            }
        }
    }

}
