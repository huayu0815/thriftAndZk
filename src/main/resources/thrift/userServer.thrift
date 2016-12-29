namespace java com.huayu.study.thriftAndZk.thrift

service UserServer{
    string getName()
    void setName(1:string name)
}