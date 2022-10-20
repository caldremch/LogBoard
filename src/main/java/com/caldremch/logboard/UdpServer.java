package com.caldremch.logboard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.Filter;
import org.springframework.integration.annotation.Router;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.ip.dsl.Udp;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.Map;

/**
 * Created by Leon on 2022/10/6
 *
 * 参考 <a href="https://blog.csdn.net/wang926454/article/details/106411016">...</a>
 */
//@Configuration
public class UdpServer {

    private static final Logger log = LoggerFactory.getLogger(UdpServer.class);


    @Value("#{new Integer('${udp.port}')}")
    private Integer port;

    @Autowired
    private UdpService udpService;

    @Bean
    @ServiceActivator(inputChannel = "udpOut")
    public IntegrationFlow udpOutFlow() {
        return f -> f.handle(Udp.outboundAdapter("localhost", 34000)
                        .configureSocket(socket -> socket.setTrafficClass(0x10)).get());
    }

    @Bean
    public IntegrationFlow integrationFlow(){
        log.info("UDP服务器启动成功, 端口号:{}", port);
        return IntegrationFlows.from(Udp.inboundAdapter(port)).channel("udpChannel").get();
    }


    /**
     * 转换器
     * @param payload
     * @param headers
     * @return
     */
    @Transformer(inputChannel = "udpChannel", outputChannel = "udpFilter")
    public String transformer(@Payload byte[] payload, @Headers Map<String, Object> headers) {
        String message = new String(payload);
        // 转换为大写
        // message = message.toUpperCase();
        // 向客户端响应，还不知道怎么写
        return message;
    }

    /**
     * 过滤器
     * @param message
     * @param headers
     * @return
     */
    @Filter(inputChannel = "udpFilter", outputChannel = "udpRouter")
    public boolean filter(String message, @Headers Map<String, Object> headers) {
//        log.info("filter");

        // 获取来源Id
        String id = headers.get("id").toString();
        // 获取来源IP，可以进行IP过滤
        String ip = headers.get("ip_address").toString();
        // 获取来源Port
        String port = headers.get("ip_port").toString();
        // 信息数据过滤
        /*if (message.indexOf("-") < 0) {
            // 没有-的数据会被过滤
            return false;
        }*/
        return true;
    }

    /**
     * 路由分发
     * @param message
     * @param headers
     * @return
     */
    @Router(inputChannel = "udpRouter")
    public String router(String message, @Headers Map<String, Object> headers) {
        // 获取来源Id
        String id = headers.get("id").toString();
        // 获取来源IP，可以进行IP过滤
        String ip = headers.get("ip_address").toString();
        // 获取来源Port
        String port = headers.get("ip_port").toString();
        // 筛选，走那个处理器
        if (false) {
            return "udpHandle2";
        }
        return "udpHandle1";
    }


    @ServiceActivator(inputChannel = "udpHandle1")
    public void udpMessageHandle(String message) throws Exception {
        log.info("udpMessageHandle-->"+message);
        if(message.equals("C1WL2202208")){
            // 可以进行异步处理
            String ipv4Address = udpService.getIPV4Address();
            if(ipv4Address != null && ipv4Address.length()>0){

            }
        }else{
//            log.info("未知协议:"+message);
        }

    }

}
