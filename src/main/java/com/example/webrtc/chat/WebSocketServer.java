package com.example.webrtc.chat;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by DELL(mxd) on 2021/2/14 19:09
 */
@ServerEndpoint("/msgServer/{userId}")
@Component
@Scope("prototype")
public class WebSocketServer {


    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     */
    private static int onlineCount = 0;
    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    private static ConcurrentHashMap<String, Session> webSocketMap = new ConcurrentHashMap<>();
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;
    /**
     * 接收userId
     */
    private String userId = "";

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        this.session = session;
        this.userId = userId;
        /**
         * 连接被打开：向socket-map中添加session
         */
        webSocketMap.put(userId, session);
        System.out.println(userId + " - 连接建立成功...");
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            this.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("连接异常...");
        error.printStackTrace();
    }

    @OnClose
    public void onClose() {
        System.out.println("连接关闭");
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        if (message.equals("心跳")){
            this.session.getBasicRemote().sendText(message);
        }
        Enumeration<String> keys = webSocketMap.keys();
        while (keys.hasMoreElements()){
            String key = keys.nextElement();
            if (key.equals(this.userId)){
                System.err.println("my id " + key);
                continue;
            }
            if (webSocketMap.get(key) == null){
                webSocketMap.remove(key);
                System.err.println(key + " : null");
                continue;
            }
            Session sessionValue = webSocketMap.get(key);
            if (sessionValue.isOpen()){
                System.out.println("发消息给: " + key + " ,message: " + message);
                sessionValue.getBasicRemote().sendText(message);
            }else {
                System.err.println(key + ": not open");
                sessionValue.close();
                webSocketMap.remove(key);
            }
        }
    }

    /**
     * 发送自定义消息
     */
    public static void sendInfo(String message, @PathParam("userId") String userId) throws IOException {
        System.out.println("发送消息到:" + userId + "，内容:" + message);
        if (!StringUtils.isEmpty(userId) && webSocketMap.containsKey(userId)) {
            webSocketMap.get(userId).getBasicRemote().sendText(message);
            //webSocketServer.sendMessage(message);
        } else {
            System.out.println("用户" + userId + ",不在线！");
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }
}
