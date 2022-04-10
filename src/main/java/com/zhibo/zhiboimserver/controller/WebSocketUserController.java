package com.zhibo.zhiboimserver.controller;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.zhibo.zhiboimserver.domain.ChatMessage;
import com.zhibo.zhiboimserver.domain.ChatMessageType;
import com.zhibo.zhiboimserver.service.ChatMessageService;
import com.zhibo.zhiboimserver.service.ChatService;
import com.zhibo.zhiboimserver.service.MemberService;
import com.zhibo.zhiboimserver.utils.JWTUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ServerEndpoint("/websocket/user")
public class WebSocketUserController {

    private static ChatService chatService;

    private static ChatMessageService chatMessageService;

    private static MemberService userService;

    @Autowired
    private void setUserService(MemberService userService){
        WebSocketUserController.userService = userService;
    }

    @Autowired
    private void setChatService(ChatService chatService){
        WebSocketUserController.chatService = chatService;
    }

    @Autowired
    private void setChatMessageService(ChatMessageService chatMessageService){
        WebSocketUserController.chatMessageService = chatMessageService;
    }

    private Integer userId;

    /**
     * 建立连接，用户上线
     * @param session
     */
    @OnOpen
    public void OnOpen(Session session) {
        String token = session.getRequestParameterMap().get("token").get(0);
        Integer userId = JWTUtil.getUserId(token);
        if (userId == null) {
            log.error("can not get userId from token:{}", token);
            return;
        }
        this.userId = userId;
        chatService.userOnline(session, userId);
        log.info("[WebSocket] 连接成功，当前userId={}", userId);
    }

    @OnClose
    public void OnClose() {
        chatService.userOffline(this.userId);
        log.info("[WebSocket] 退出成功，当前userId={}", userId);
    }

    @OnMessage
    public void OnMessage(String message) {
        log.info("[WebSocket] userSendMessage：{}", message);
        ChatMessage chatMessage = JSONObject.parseObject(message, ChatMessage.class);
        chatMessage.setSenderId(this.userId);
        chatMessage.setSenderType(ChatMessageType.USER);
        chatMessageService.userSendMessage(chatMessage);
    }
}
