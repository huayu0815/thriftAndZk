package com.huayu.study.thriftAndZk.zkStudy;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMultiLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.RetryNTimes;

import java.util.concurrent.TimeUnit;

/**
 * Created by zhaohuayu on 16/12/29.
 * 基于zk的分布式锁的大致实现流程:每个client在对应的节点上创建一个瞬时的有序节点,通过判断是否是有序节点中最小的一个
 * 来判断是否获取锁。释放锁时,把瞬时节点删除即可。curator作为zk的第三方类库,提供了较为方便的api
 */
public class CuratorDistrLock {
    public static final String ZK_ADDRESS = "10.94.96.190:2181" ;
    public static final String ZK_LOCK_PATH = "/zktest" ;

    public static void main(String[] args) throws Exception {

        //1.connect to zk
        final CuratorFramework client = CuratorFrameworkFactory.newClient(ZK_ADDRESS, new RetryNTimes(10, 5000));
        client.start();

        Thread t1 = new Thread(new Runnable() {
            public void run() {
                doWithLock(client);
            }
        }, "t1") ;

        Thread t2 = new Thread(new Runnable() {
            public void run() {
                doWithLock(client);
            }
        }, "t2") ;

        t1.start();
        t2.start();
    }

    public static void doWithLock(CuratorFramework client) {
        //创建基于zk_lock_path的锁
        InterProcessMutex lock = new InterProcessMutex(client, ZK_LOCK_PATH) ;
        try {
            //尝试获取锁(10S后超时,如果没有拿到,返回false,拿到,返回true)
            if (lock.acquire(10*1000, TimeUnit.SECONDS)) {
                System.out.println(Thread.currentThread().getName() + "hold lock");
                Thread.sleep(5000L);
                System.out.println(Thread.currentThread().getName() + "release lock");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                lock.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
