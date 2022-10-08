package com.caldremch.logboard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

/**
 * Created by Leon on 2022/10/6
 */
@Component
public class MultiUdpServer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(MultiUdpServer.class);


    @Value("#{new Integer('${udp.port}')}")
    private Integer port;
    @Value("#{new Integer('${server.port}')}")
    private Integer serverPort;

    @Autowired
    private UdpService udpService;


    @Override
    public void run(String... args) throws Exception {
        simpleUdpServer();
    }

    MultiUdpServer simpleUdpServer() throws IOException {
        //创建通道和选择器
        Selector selector = Selector.open();
        log.info("==================udp服务端启动成功 ====================");
        int[] ports = new int[10];
        for (int i = 0; i < 10; i++) {
            ports[i] = port + i;
        }
        for (int i = 0; i < ports.length; i++) {
            DatagramChannel datagramChannel = DatagramChannel.open();
            //设置为非阻塞模式
            datagramChannel.configureBlocking(false);
            //创建data socket
            DatagramSocket datagramSocket = datagramChannel.socket();
            //绑定端口
            datagramSocket.bind(new InetSocketAddress(ports[i]));
            //将通道注册至selector
            datagramChannel.register(selector, SelectionKey.OP_READ);
        }
        //接收客户端发送的数据
        new Thread(() -> {
            while (true) {
                try {
                    while (selector.select() > 0) {
                        //获取选择器
                        Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                        //遍历选择器
                        while (iterator.hasNext()) {
                            SelectionKey selectionKey = (SelectionKey) iterator.next();
                            if (selectionKey.isReadable()) {
                                //释放端口
                                iterator.remove();
                                //接收数据
                                doReceive(selectionKey);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return new MultiUdpServer();
    }

    private void doReceive(SelectionKey selectionKey) throws InterruptedException, IOException {
        String str = "";
        DatagramChannel datagramChannel = (DatagramChannel) selectionKey.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        InetSocketAddress socketAddress = (InetSocketAddress) datagramChannel.receive(buffer);
        buffer.flip();
        str = new String(buffer.array(), 0, buffer.limit());
//        log.info(str);
        if (str.equalsIgnoreCase("C1WL2202208")) {
            // 可以进行异步处理
            String ipv4Address = udpService.getIPV4Address();
//            log.info(ipv4Address);
            if (ipv4Address != null && ipv4Address.length() > 0) {
                String ip = socketAddress.getAddress().getHostAddress();
                int port = socketAddress.getPort();
//                log.info("客户端:{}:{}", ip, port);
                String replayData = ipv4Address+":"+serverPort;
                byte[]repayBuffer = replayData.getBytes();
                ByteBuffer repayByteBuffer = ByteBuffer.wrap(repayBuffer);
                datagramChannel.send(repayByteBuffer, new InetSocketAddress(ip, port));
            }
        } else {
            log.info("未知协议:" + str);
        }

        buffer.clear();
    }

//    29916823

}
