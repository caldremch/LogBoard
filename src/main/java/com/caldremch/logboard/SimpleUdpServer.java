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

/**
 * Created by Leon on 2022/10/6
 */
@Component
public class SimpleUdpServer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SimpleUdpServer.class);


    @Value("#{new Integer('${udp.port}')}")
    private Integer port;
    @Value("#{new Integer('${server.port}')}")
    private Integer serverPort;

    @Autowired
    private UdpService udpService;

    private DatagramSocket datagramSocket = null;

    @Override
    public void run(String... args) throws Exception {
        simpleUdpServer();
    }

    SimpleUdpServer simpleUdpServer() {
        try {

            datagramSocket = new DatagramSocket(port);
            log.info("UDP服务器启动成功, 端口号:{}", port);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        byte[] buffer = new byte[1024]; // 最大65507个字节
                        DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                        try {
                            datagramSocket.receive(datagramPacket);
                            /**
                             * 客户端信息
                             */
                            String ip = datagramPacket.getAddress().getHostAddress();
                            int port = datagramPacket.getPort();

                            /**
                             * 读取数据
                             */
                            String data = new String(datagramPacket.getData(), 0, datagramPacket.getLength());

//                           log.info("server recv: ip=" + ip + ", port=" + port + ", data=" + data);

                            /**
                             * 响应客户端
                             */
                            if (data.equalsIgnoreCase("C1WL2202208")) {
                                // 可以进行异步处理
                                String ipv4Address = udpService.getIPV4Address();
                                if (ipv4Address != null && ipv4Address.length() > 0) {
                                    String replayData = ipv4Address+":"+serverPort;
                                    buffer = replayData.getBytes();
                                    datagramSocket.send(new DatagramPacket(buffer, 0, buffer.length, new InetSocketAddress(ip, port)));
                                }
                            } else {
                                log.info("未知协议:" + data);
                            }

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }).start();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new SimpleUdpServer();
    }


    public void udpMessageHandle(String message) throws Exception {
        log.info("udpMessageHandle-->" + message);
        if (message.equals("C1WL2202208")) {
            // 可以进行异步处理
            String ipv4Address = udpService.getIPV4Address();
            if (ipv4Address != null && ipv4Address.length() > 0) {
            }
        } else {
//            log.info("未知协议:"+message);
        }

    }

}
