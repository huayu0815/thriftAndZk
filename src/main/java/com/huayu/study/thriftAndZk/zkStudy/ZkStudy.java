package com.huayu.study.thriftAndZk.zkStudy;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

/**
 * Created by zhaohuayu on 16/12/28.
 */
public class ZkStudy {

    /**
     * watcher 每触发一次,之前的关联的监听就会失效,需要重新设置watcher
     * @param args
     * @throws Exception
     */

    public static void main(String[] args) throws Exception {
        ZooKeeper zk ;
        try {
            zk = new ZooKeeper("10.94.96.190:2181", 300000000, new Watcher() {
                public void process(WatchedEvent watchedEvent) {
                    System.out.println("state:" + watchedEvent.getState());
                    System.out.println("path:" + watchedEvent.getPath());
                    System.out.println("type:" + watchedEvent.getType());
                }
            }) ;

            Stat stat = zk.exists("/root", true) ;

            if (stat == null) {
                zk.create("/root", "testData".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT) ;
            }
            System.out.println(new String(zk.getData("/root", true, null)));

            zk.setData("/root", "new data".getBytes(), -1) ;

            System.out.println(new String(zk.getData("/root", true, null)));

            Stat stat1 = zk.exists("/root/1", true) ;

            if (stat1 == null) {
                zk.create("/root/1", "testData".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT) ;
            }

            System.out.println(new String(zk.getData("/root/1", true, null)));

            System.out.println(zk.getChildren("/root", true));

            Stat stat2 = zk.exists("/root/2", true) ;

            if (stat2 == null) {
                zk.create("/root/2", "testData".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT) ;
            }

            System.out.println(zk.getChildren("/root", true));

            zk.delete("/root/2", -1);

            System.out.println(zk.getChildren("/root", false));

            zk.delete("/root/1", -1);

            System.out.println(zk.getChildren("/root", true));

            zk.delete("/root", -1);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
