package com.huayu.study.thriftAndZk.thriftStudy.client;

import com.huayu.study.thriftAndZk.thriftStudy.thrift.UserServer;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhaohuayu on 16/12/31.
 */
public class UserServerClient {

    public static final String SERVER_IP = "localhost";

    public static final int SERVER_PORT = 5200;

    public static final int TIMEOUT = 30000;

    public static void startAsynClient() throws Exception {
        TAsyncClientManager clientManager = new TAsyncClientManager();
        TNonblockingTransport transport = new TNonblockingSocket(SERVER_IP, SERVER_PORT, TIMEOUT);
        TProtocolFactory tprotocol = new TCompactProtocol.Factory();
        UserServer.AsyncClient asyncClient = new UserServer.AsyncClient(tprotocol, clientManager, transport);
        CountDownLatch latch = new CountDownLatch(1);

        AsynCallback callBack = new AsynCallback(latch);

        System.out.println("call method sayHello start ...");

        asyncClient.sayHelloWorld("test", callBack);

        System.out.println("call method sayHello .... end");

        boolean wait = latch.await(30, TimeUnit.SECONDS);

        System.out.println("latch.await =:" + wait);

        System.out.println("Client start .....");

    }

    public static void startSyncClient() throws TException {
        TTransport tTransport = new TFramedTransport(new TSocket(SERVER_IP, SERVER_PORT, TIMEOUT)) ;
        TProtocol protocol = new TCompactProtocol(tTransport) ;
        UserServer.Client client = new UserServer.Client(protocol);
        tTransport.open();
        client.sayHelloWorld("sync");
        System.out.println(client.getName());
        tTransport.close();
    }

    static class AsynCallback implements AsyncMethodCallback<UserServer.AsyncClient.sayHelloWorld_call> {
        private CountDownLatch latch;
        public AsynCallback(CountDownLatch latch) {
            this.latch = latch;
        }
        @Override
        public void onComplete(UserServer.AsyncClient.sayHelloWorld_call response) {
            System.out.println("onComplete");
            try {
                // Thread.sleep(1000L * 1);
                System.out.println("ok");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        }

        @Override
        public void onError(Exception exception) {
            System.out.println("onError :" + exception.getMessage());
            latch.countDown();
        }
    }

    public static void main(String[] args) throws Exception {
        startAsynClient();
        //startSyncClient();
    }
}
