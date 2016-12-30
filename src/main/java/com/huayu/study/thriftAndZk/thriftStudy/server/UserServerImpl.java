package com.huayu.study.thriftAndZk.thriftStudy.server;

import com.huayu.study.thriftAndZk.thriftStudy.thrift.UserServer;
import org.apache.thrift.TException;

/**
 * Created by zhaohuayu on 16/12/30.
 */
public class UserServerImpl implements UserServer.Iface {

    private String name = "default";

    public void sayHelloWorld(String name) throws TException {
        System.out.println("hello world!" + name);
        this.name = name ;
    }

    public String getName() throws TException {
        return name;
    }
}
