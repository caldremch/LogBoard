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
@ServerEndpoint(value = "/websocket")
@Component
public class LogWebSocket {

    private static Map<String,LogWebSocket> webSocketMap = new LinkedHashMap<>();

    private final static JsonParser jsonParser = new JsonParser();

    private static int count=0;

    private Session session;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    //处理连接建立
    @OnOpen
    public void onOpen(Session session){
        this.session=session;
        webSocketMap.put(session.getId(),this);
        addCount();
        log.info("新加入客户端：sessionId={}, 当前总连接数:{}",session.getId(), count);
        try {
            sendMessage("已连接");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    @OnError
    public void onError(Throwable error,Session session){
        if(error.getMessage() != null && !error.getMessage().isEmpty()){
            log.info("发生错误{},{}",session.getId(),error.getMessage());
        }
    }

    @OnClose
    public void onClose(){
        webSocketMap.remove(this.session.getId());
        reduceCount();
        log.info("[客户端退出:{}]:退出,  当前总连接数:{}",this.session.getId(), count);
    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }


    public static int getCount(){
        return count;
    }

    public static synchronized void addCount(){
        LogWebSocket.count++;
    }

    public static synchronized void reduceCount(){
        LogWebSocket.count--;
    }

}
