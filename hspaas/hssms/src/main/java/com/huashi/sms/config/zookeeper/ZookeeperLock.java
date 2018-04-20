package com.huashi.sms.config.zookeeper;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO Zookeeper分布式锁
 * 
 * @author zhengying
 * @version V1.0
 * @date 2018年3月26日 下午10:59:31
 */
// @Component
public class ZookeeperLock implements Lock {

    /**
     * 锁的根目录
     */
    private String         rootLockPath;
    
   /**
    * 分布式锁的自定义路径（节点锁） 
    */
    private String         tmpLockPath;

    private ZkClient       client = null;

    private static Logger  logger = LoggerFactory.getLogger(ZookeeperLock.class);

    private CountDownLatch cdl;

    /**
     * 当前请求的节点
     */
    private String         beforePath;

    /**
     * 当前请求的节点前一个节点
     */
    private String         currentPath;
    
    /**
     * 默认会话超时时间（毫秒）
     */
    private static final int DEFAULT_SESSION_TIMEOUT = 15 * 1000;
    
    /**
     * 默认连接超时时间（毫秒）
     */
    private static final int DEFAULT_CONNECT_TIMEOUT = 15 * 1000;
    
    /**
     * 
      *@param zkConnectUrl
      *@param sessionTimeout
      *@param connectTimeout
      *     连接超时时间（毫秒）
      *@param rootLockPath
      *@param tmpLockPath
     */
    public ZookeeperLock(String zkConnectUrl, int sessionTimeout, int connectTimeout, String rootLockPath, String tmpLockPath) {
        this.rootLockPath = rootLockPath;
        this.tmpLockPath = tmpLockPath;

        client = new ZkClient(zkConnectUrl, sessionTimeout, connectTimeout, new SerializableSerializer());

        // 判断有没有LOCK目录，没有则创建
        if (!this.client.exists(rootLockPath)) {
            this.client.createPersistent(rootLockPath);
        }
    }

    /**
     * 
      *@param zkConnectUrl
      *@param rootLockPath
      *@param tmpLockPath
     */
    public ZookeeperLock(String zkConnectUrl, String rootLockPath, String tmpLockPath) {
        this.rootLockPath = rootLockPath;
        this.tmpLockPath = tmpLockPath;

        client = new ZkClient(zkConnectUrl, DEFAULT_SESSION_TIMEOUT, DEFAULT_CONNECT_TIMEOUT, new SerializableSerializer());

        // 判断有没有LOCK目录，没有则创建
        if (!this.client.exists(rootLockPath)) {
            this.client.createPersistent(rootLockPath);
        }
    }
    

    public boolean tryLock() {
        // 如果currentPath为空则为第一次尝试加锁，第一次加锁赋值currentPath
        if (currentPath == null || currentPath.length() <= 0) {
            // 创建一个临时顺序节点
            currentPath = this.client.createEphemeralSequential(rootLockPath + '/', tmpLockPath);
            System.out.println("---------------------------->" + currentPath);
        }
        // 获取所有临时节点并排序，临时节点名称为自增长的字符串如：0000000400
        List<String> childrens = this.client.getChildren(rootLockPath);
        Collections.sort(childrens);

        // 如果当前节点在所有节点中排名第一则获取锁成功
        if (currentPath.equals(rootLockPath + '/' + childrens.get(0))) {
            return true;
        } else {

            // 如果当前节点在所有节点中排名中不是排名第一，则获取前面的节点名称，并赋值给beforePath
            int wz = Collections.binarySearch(childrens, currentPath.substring(6));
            beforePath = rootLockPath + '/' + childrens.get(wz - 1);
        }
        return false;
    }

    public void unlock() {
        // 删除当前临时节点
        client.delete(currentPath);

    }

    public void lock() {
        if (!tryLock()) {
            waitForLock();
            lock();
        } else {
            logger.info(Thread.currentThread().getName() + " 获得分布式锁！");
        }

    }

    private void waitForLock() {
        IZkDataListener listener = new IZkDataListener() {

            public void handleDataDeleted(String dataPath) throws Exception {
                logger.info(Thread.currentThread().getName() + ":捕获到DataDelete事件！---------------------------");
                if (cdl != null) {
                    cdl.countDown();
                }
            }

            public void handleDataChange(String dataPath, Object data) throws Exception {

            }
        };
        // 给排在前面的的节点增加数据删除的watcher
        this.client.subscribeDataChanges(beforePath, listener);

        if (this.client.exists(beforePath)) {
            cdl = new CountDownLatch(1);
            try {
                cdl.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.client.unsubscribeDataChanges(beforePath, listener);
    }

    // ==========================================

    public void lockInterruptibly() throws InterruptedException {

    }

    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    public Condition newCondition() {
        return null;
    }

}
