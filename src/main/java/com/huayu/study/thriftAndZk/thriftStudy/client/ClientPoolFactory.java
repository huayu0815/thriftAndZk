package com.huayu.study.thriftAndZk.thriftStudy.client;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.thrift.TServiceClient;

/**
 * Created by zhaohuayu on 16/12/31.
 */
public class ClientPoolFactory {

    private GenericObjectPool<TServiceClient> pool;

    public ClientPoolFactory(GenericObjectPool.Config config) {
        pool = new GenericObjectPool<TServiceClient>(new ConnectionFactory(), config) ;
    }

    public TServiceClient getClient() throws Exception {
        System.out.println(pool.getNumActive()); ;
        return pool.borrowObject() ;
    }

    public void releaseConnection(TServiceClient client) throws Exception {
        pool.returnObject(client);
    }
}
