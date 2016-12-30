namespace java com.huayu.study.thriftAndZk.thriftStudy.thrift

service UserServer{
    void sayHelloWorld(1:string name)
    string getName()
}