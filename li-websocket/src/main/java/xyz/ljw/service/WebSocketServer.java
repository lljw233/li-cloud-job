package xyz.ljw.service;


import com.alibaba.fastjson2.JSON;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
@Component
@ServerEndpoint("/ljw/websocket/{userId}")
public class WebSocketServer {

    private Session session;

    private String userId;

    private static ConcurrentHashMap<String, Session> webSocketMap = new ConcurrentHashMap<>();

    private static AtomicInteger onlineCount = new AtomicInteger(0);

    @OnOpen
    public void open(Session session, @PathParam("userId")String userId) {
        this.session = session;
        this.userId = userId;
        //
        webSocketMap.put(userId, session);
        onlineCount.incrementAndGet();
        int count = onlineCount.get();
        System.out.println("当前在线人数为：" + count);
    }



    @OnClose
    public void close() {
        if (webSocketMap.contains(userId)) {
            webSocketMap.remove(userId);
        }
        onlineCount.decrementAndGet();
        System.out.println("当前在线人数为：" + onlineCount.get());
    }

    @OnMessage
    public void message(String message,Session session) {
        System.out.println("用户：" + this.userId +"," + "消息为：" + message);
        // 解析出消息，看下消息要转发给谁
        Map<String,String> messageJson = JSON.parseObject(message, Map.class);
        System.out.println(message);
        // 判断消息给谁
        System.out.println(messageJson.get("userId"));
        try {
            if (webSocketMap.containsKey(messageJson.get("userId"))) {
                Map<String, String> msg = new HashMap<>();
                msg.put("userId", this.userId);
                msg.put("msg", messageJson.get("msg"));
                webSocketMap.get(messageJson.get("userId")).getBasicRemote().sendText(JSON.toJSONString(msg));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}
