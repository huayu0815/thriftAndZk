package com.huayu.study.thriftAndZk.zkStudy;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.data.Stat;

/**
 * Created by zhaohuayu on 16/12/29.
 */
public class CuratorStudy {

    public static final String ZK_ADDRESS = "10.94.96.190:2181" ;
    public static final String ZK_PATH = "/zktest" ;

    public static void main(String[] args) throws Exception {

        //1.connect to zk
        CuratorFramework client = CuratorFrameworkFactory.newClient(ZK_ADDRESS, new RetryNTimes(10, 5000)) ;
        client.start();

        /**
         * Curator提供了三种Watcher(Cache)来监听结点的变化：

         Path Cache：监视一个路径下1）孩子结点的创建、2）删除，3）以及结点数据的更新。产生的事件会传递给注册的PathChildrenCacheListener。
         Node Cache：监视一个结点的创建、更新、删除，并将结点的数据缓存在本地。
         Tree Cache：Path Cache和Node Cache的“合体”，监视路径下的创建、更新、删除事件，并缓存路径下所有孩子结点的数据。
         第三个参数:cacheData 用于配置是否把节点内容缓存起来，如果配置为true，那么客户端在接
         收到节点列表变更的同时，也能够获取到节点的数据内容,如果为false
         则无法取到数据内容
         */
        //2 register watcher
        PathChildrenCache watcher = new PathChildrenCache(client, "/", true) ;
        watcher.getListenable().addListener(new PathChildrenCacheListener() {
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                ChildData data = pathChildrenCacheEvent.getData() ;
                if (data == null) {
                    System.out.println("No data in event [" + pathChildrenCacheEvent + "]");
                } else {
                    System.out.println("Receive event:"
                    + "type=[" + pathChildrenCacheEvent.getType() + "],"
                            + "type=[" + pathChildrenCacheEvent.getType() + "],"
                            + "path=[" + data.getPath() + "],"
                            + "data=[" + new String(data.getData()) + "],"
                            + "stat=[" + data.getStat() + "],"
                    );
                }
            }
        });

        /**
         * PathChildrenCache.StartMode
         • BUILD_INITIAL_CACHE //同步初始化客户端的cache，及创建cache后，就从服务器端拉入对应的数据
         • NORMAL //异步初始化cache
         • POST_INITIALIZED_EVENT //异步初始化，初始化完成触发事件PathChildrenCacheEvent.Type.INITIALIZED
         */
        watcher.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);

        //2.client api
        //2.1 create node
        Stat stat = client.checkExists().forPath(ZK_PATH) ;
        if(stat == null) {
            client.create().creatingParentContainersIfNeeded().forPath(ZK_PATH, "pathData".getBytes()) ;
        }

        //2.2 get node and data
        System.out.println(client.getChildren().forPath("/"));
        System.out.println(new String(client.getData().forPath(ZK_PATH)));

        //2.3 modify data
        client.setData().forPath(ZK_PATH, "pathData2".getBytes()) ;
        System.out.println(new String(client.getData().forPath(ZK_PATH)));

        //2.4 remove node
        client.delete().forPath(ZK_PATH) ;
        System.out.println(client.getChildren().forPath("/"));

    }
}
