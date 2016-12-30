# thriftAndZk

## thrift server类型说明
1) TSimpleServer
单线程阻塞IO方式工作,每次接受一个连接,直到客户端关闭后才能接受新的连接。用于测试环境
2) TThreedPoolServer
使用专门的线程池来接受连接,一旦接受连接,会放到ThreadPoolExecutor中的线程中执行,
worker线程被绑定到特定的客户端连接上,直到客户端关闭。
**缺点是:** 客户端多的话,服务端就需要开启N多
线程,容易造成服务器压力过大
3) TNonblockingServer
本质上使用JAVA nio实现(java.nio.channels.Selector),通过调用select(),内部循环
不断监听各个连接的状态,当一个或多个连接准备好被接受/读/写时,select()调用返回。该方式
服务端可以同时处理多个客户端请求而不会被一个客户端把其他客户端"饿死"。
**缺点是:** 当一个消息被处理时候,其他消息要等待,效率低下
4) THsHaServer
java NIO + 线程池实现。一个单独的线程处理连接,一个单独的线程池处理消息
5) TThreadedSelectorServer
维护两个线程池,一个处理连接,一个处理消息,当网络IO是瓶颈的时候,该类型性能明显优于THshaServer

## 服务端编码基本步骤
1) 实现服务处理接口impl
2) 创建TProcessor
3) 创建TServerTransport
4) 创建TProtocol
5) 创建TServer
6) 启动Server

## 客户端编码基本步骤：
1) 创建Transport
2) 创建TProtocol
3) 基于TTransport和TProtocol创建 Client
4) 调用Client的相应方法

## 数据传输协议
1) TBinaryProtocol : 二进制格式.
2) TCompactProtocol : 压缩格式
3) TJSONProtocol : JSON格式
4) TSimpleJSONProtocol : 提供JSON只写协议, 生成的文件很容易通过脚本语言解析


# common-pool 连接池

common-pool内部一个LinkList的池子和一些计数的变量。每次获取connection的时候,先判断是不是达到
maxActive: 链接池中最大连接数。达到了则等待,不然看pool中是否有可以使用的connection,有则返回。
没有则创建。每次获取connection或是把connection放回pool的时候,会检测connection是否可用。

主要类及方法
- GenericObjectPool  池子
1) **Object borrowObject()** : 从Pool获取一个对象,此操作将导致一个"对象"从Pool移除(脱离Pool管理),
调用者可以在获得"对象"引用后即可使用,且需要在使用结束后"归还"
2) **void returnObject(Object obj)** : "归还"对象,当"对象"使用结束后,需要归还到Pool中,才能维持
Pool中对象的数量可控,如果不归还到Pool,那么将意味着在Pool之外,将有大量的"对象"存在,那么就使用了"对
象池"的意义
- 对象工厂**PoolableObjectFactory**接口
1) **Object makeObject()** : 创建一个新对象;当对象池中的对象个数不足时,将会使用此方法来"输出"一个新
的"对象",并交付给对象池管理
2) **void destroyObject(Object obj)** : 销毁对象,如果对象池中检测到某个"对象"idle的时间超时,或者
操作者向对象池"归还对象"时检测到"对象"已经无效,那么此时将会导致"对象销毁";"销毁对象"的操作设计相差甚
远,但是必须明确:当调用此方法时,"对象"的生命周期必须结束.如果object是线程,那么此时线程必须退出;如果
object是socket操作,那么此时socket必须关闭;如果object是文件流操作,那么此时"数据flush"且正常关闭.
3) **boolean validateObject(Object obj)** : 检测对象是否"有效";Pool中不能保存无效的"对象",因此
"后台检测线程"会周期性的检测Pool中"对象"的有效性,如果对象无效则会导致此对象从Pool中移除,并destroy;
此外在调用者从Pool获取一个"对象"时,也会检测"对象"的有效性,确保不能讲"无效"的对象输出给调用者;当调用者
使用完毕将"对象归还"到Pool时,仍然会检测对象的有效性.所谓有效性,就是此"对象"的状态是否符合预期,是否可
以对调用者直接使用;如果对象是Socket,那么它的有效性就是socket的通道是否畅通/阻塞是否超时等.
4) **void activateObject(Object obj)** : "激活"对象,当Pool中决定移除一个对象交付给调用者时额外的
"激活"操作,比如可以在activateObject方法中"重置"参数列表让调用者使用时感觉像一个"新创建"的对象一样;
如果object是一个线程,可以在"激活"操作中重置"线程中断标记",或者让线程从阻塞中唤醒等;如果 object是一
个socket,那么可以在"激活操作"中刷新通道,或者对socket进行链接重建(假如socket意外关闭)等.
5) **void void passivateObject(Object obj)** : "钝化"对象,当调用者"归还对象"时,Pool将会"钝化对
象";钝化的言外之意,就是此"对象"暂且需要"休息"一下.如果object是一个 socket,那么可以passivateObject
中清除buffer,将socket阻塞;如果object是一个线程,可以在"钝化"操作中将线程sleep或者将线程中的某个对象
wait.需要注意的时,activateObject和passivateObject两个方法需要对应,避免死锁或者"对象"状态的混乱.

*经过实践,线程池的最大链接数在高并发的时候并不能完全保证数量的控制。比如同时100个并发,在pool.borrowObject()
的时候,认为池子中可用的connection为0,而当前活跃的connection为0,就会同时创建近100个connection。*

