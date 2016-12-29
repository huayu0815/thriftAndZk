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
