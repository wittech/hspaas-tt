package com.huashi.sms.test.lock;

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
 * TODO ZooKeeper分布式锁
 * 
 * @author zhengying
 * @version V1.0
 * @date 2018年3月23日 下午6:07:58
 */

//@Component
public class ZkDistributeLock implements Lock {

    private ZkClient            client         = new ZkClient("127.0.0.1:2181", 1000, 1000, new SerializableSerializer());

    private static Logger       logger         = LoggerFactory.getLogger(ZkDistributeLock.class);

    private CountDownLatch      cdl;

    /**
     * 当前请求的节点
     */
    private String              beforePath;

    /**
     * 当前请求的节点前一个节点
     */
    private String              currentPath;

//    @Value("${zk.connect}")
//    private String              zkConnectUrl = "127.0.0.1:2181";

//    @Value("${zk.locknode}")
    private String              zkLockNode = "LOCK";
    
  //判断有没有LOCK目录，没有则创建
    public ZkDistributeLock() {
        if (!this.client.exists(zkLockNode)) {
            this.client.createPersistent(zkLockNode);
        }
    }

    /**
     * 临时节点
     */
    private static final String TEMP_LOCK_NODE = "lock";

//    @PostConstruct
//    public void initial() {
//        client = new ZkClient(zkConnectUrl, 1000, 1000, new SerializableSerializer());
//
//        // 判断有没有LOCK目录，没有则创建
//        if (!client.exists(zkLockNode)) {
//            client.createPersistent(zkLockNode);
//        }
//        
//        System.out.println("执行了.....");
//    }

    public boolean tryLock() {
        // 如果currentPath为空则为第一次尝试加锁，第一次加锁赋值currentPath
        
        if (currentPath == null || currentPath.length() <= 0) {
            // 创建一个临时顺序节点
            currentPath = this.client.createEphemeralSequential(zkLockNode + '/', TEMP_LOCK_NODE);
            System.out.println("---------------------------->" + currentPath);
        }
        // 获取所有临时节点并排序，临时节点名称为自增长的字符串如：0000000400
        List<String> childrens = this.client.getChildren(zkLockNode);
        Collections.sort(childrens);

        if (currentPath.equals(zkLockNode + '/' + childrens.get(0))) {// 如果当前节点在所有节点中排名第一则获取锁成功
            return true;
        } else {// 如果当前节点在所有节点中排名中不是排名第一，则获取前面的节点名称，并赋值给beforePath
            int wz = Collections.binarySearch(childrens, currentPath.substring(6));
            beforePath = zkLockNode + '/' + childrens.get(wz - 1);
        }
        return false;
        
//        return true;
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

    public void lockInterruptibly() throws InterruptedException {
    }

    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    public Condition newCondition() {
        return null;
    }

}
