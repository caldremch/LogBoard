package com.caldremch.logboard;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import sun.tools.jinfo.JInfo;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Leon on 2022/7/22
 */
@ServerEndpoint(value = "/websocket") //接受websocket请求路径
@Component  //注册到spring容器中
public class LogWebSocket {

    //保存所有在线socket连接
    private static Map<String,LogWebSocket> webSocketMap = new LinkedHashMap<>();

    private final static JsonParser jsonParser = new JsonParser();

    //记录当前在线数目
    private static int count=0;

    //当前连接（每个websocket连入都会创建一个MyWebSocket实例
    private Session session;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    static {
    }
    //处理连接建立
    @OnOpen
    public void onOpen(Session session){
        this.session=session;
        webSocketMap.put(session.getId(),this);
        addCount();
        log.info("新的连接加入：{}",session.getId());
    }

    private final static Gson gson = new Gson();

    //接受消息
    @OnMessage
    public void onMessage(String message,Session session){

        try {
            byte[] bytes = message.getBytes();
            int level = bytes[0];
            byte[] msgBytes = new byte[bytes.length - 1];
            System.arraycopy(bytes, 1, msgBytes, 0, msgBytes.length);
            String msg = new String(msgBytes);
//            log.info("level={}", level);
//            log.info("msg={}", msg);
            switch (level){
                case 0:
                    log.debug(msg);
                    break;
                case 1:
                    log.error(msg);
                    break;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //处理错误
    @OnError
    public void onError(Throwable error,Session session){
        log.info("发生错误{},{}",session.getId(),error.getMessage());
    }

    //处理连接关闭
    @OnClose
    public void onClose(){
        webSocketMap.remove(this.session.getId());
        reduceCount();
        log.info("连接关闭:{}",this.session.getId());
    }

    //群发消息

    //发送消息
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    //广播消息
    public static void broadcast(){
        LogWebSocket.webSocketMap.forEach((k,v)->{
            try{
                v.sendMessage("这是一条测试广播");
            }catch (Exception e){
            }
        });
    }

    //获取在线连接数目
    public static int getCount(){
        return count;
    }

    //操作count，使用synchronized确保线程安全
    public static synchronized void addCount(){
        LogWebSocket.count++;
    }

    public static synchronized void reduceCount(){
        LogWebSocket.count--;
    }

}
