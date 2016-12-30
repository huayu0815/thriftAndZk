package com.huayu.study.thriftAndZk.thriftStudy.client;

import com.huayu.study.thriftAndZk.thriftStudy.thrift.UserServer;

/**
 * Created by zhaohuayu on 16/12/31.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        final ClientProxyFactory clientProxyFactory = new ClientProxyFactory() ;

        for (int i=0; i<5;i++) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        UserServer.Client client = (UserServer.Client) clientProxyFactory.getClient() ;
                        //Thread.sleep(10000L);
                        System.out.println(client.toString());
                        client.sayHelloWorld("aaa" + Thread.currentThread().getName());
                        //System.out.println("bbb" + client.getName()) ;
                        Thread.sleep(10000);

                        clientProxyFactory.releaseConnection(client);
                        Thread.sleep(10000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }, "Thread"+ i).start();
            Thread.sleep(1000L);
        }
        Thread.sleep(100000000000L);

    }
}
