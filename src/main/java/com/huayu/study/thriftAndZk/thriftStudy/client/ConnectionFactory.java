package com.huayu.study.thriftAndZk.thriftStudy.client;

import com.huayu.study.thriftAndZk.thriftStudy.thrift.UserServer;
import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Created by zhaohuayu on 16/12/31.
 */
public class ConnectionFactory extends BasePoolableObjectFactory {

    Logger logger = LoggerFactory.getLogger(ConnectionFactory.class) ;

    public final String SERVER_IP = "localhost";

    public final int SERVER_PORT = 5200;

    public final int TIMEOUT = 30000;

    public Object makeObject() throws Exception {
        TTransport tTransport = new TFramedTransport(new TSocket(SERVER_IP, SERVER_PORT, TIMEOUT)) ;
        TProtocol protocol = new TCompactProtocol(tTransport) ;
        UserServer.Client client = new UserServer.Client(protocol);
        tTransport.open();
        logger.info("add new object");
        return client ;
    }

    public void destroyObject(Object obj) throws Exception  {
        if(obj instanceof TServiceClient){
            ((TServiceClient)obj).getInputProtocol().getTransport().close();
            ((TServiceClient)obj).getOutputProtocol().getTransport().close();
            logger.info("destroy object");
        }
    }

    public boolean validateObject(Object obj) {
        if(obj instanceof TServiceClient){
            TTransport pin = ((TServiceClient)obj).getInputProtocol().getTransport();
            logger.info("validateObject input:{}", pin.isOpen());
            TTransport pout = ((TServiceClient)obj).getOutputProtocol().getTransport();
            logger.info("validateObject output:{}", pout.isOpen());
            return pin.isOpen() && pout.isOpen();
        }
        return false ;

    }


}
