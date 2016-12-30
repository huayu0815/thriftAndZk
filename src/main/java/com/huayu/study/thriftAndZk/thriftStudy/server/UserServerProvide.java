package com.huayu.study.thriftAndZk.thriftStudy.server;

import com.huayu.study.thriftAndZk.thriftStudy.thrift.UserServer;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.THsHaServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransport;

/**
 * Created by zhaohuayu on 16/12/30.
 */
public class UserServerProvide {

    private static final int SERVER_PORT = 5200;

    public static void startServer() throws Exception {
        //创建TProcessor
        TProcessor tProcessor = new UserServer.Processor<UserServer.Iface>(new UserServerImpl()) ;

        //创建TServerTransport
        //使用非阻塞式IO，服务端和客户端需要指定TFramedTransport数据传输的方式
        TFramedTransport.Factory tServerTransport = new TFramedTransport.Factory();

        //创建TProtocol
        TCompactProtocol.Factory protocalFactory = new TCompactProtocol.Factory() ;

        //创建TServer
        //设置协议端口
        TNonblockingServerSocket tnbSocketServerSokcet = new TNonblockingServerSocket(SERVER_PORT);

        //使用异步方式,client也使用异步方式
        THsHaServer.Args tArgs = new THsHaServer.Args(tnbSocketServerSokcet) ;

        tArgs.processor(tProcessor) ;
        tArgs.transportFactory(tServerTransport) ;
        tArgs.protocolFactory(protocalFactory) ;

        //创建tserver
        TServer server = new THsHaServer(tArgs);
        //服务启动
        server.serve();

        //注册ZK


    }

    public static void main(String[] args) throws Exception {
        startServer();
    }


}
