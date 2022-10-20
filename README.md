
# Websocket服务器

用于接收websocket网络消息

## 特点

1. 添加udp来做探测客户端.所以并不需要指定ip, 别面改动设置

application.properties配置如下

```properties
# 任何ip来源都接受
server.address = 0.0.0.0
#websocket端口
server.port=34001
#udp 端口
udp.port=34000
```


# 问题记录

## 1.传递的数据过大的, 而且出现json失败
网络请求的数据都比较复杂, 所以不适合做json数据的传递, 而是直接传递一整个数据包
解决思路:
在数据源前后拼接一个字节, 然后传递, 然后读取关键字节即可做到错误即可

## 2.注意连接不通
检查ip, 检查电脑或者手机是否开启了代理软件, 如果开启了就关闭掉