package com.zhibo.zhiboimserver.controller;

import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.zhibo.zhiboimserver.domain.Chat;
import com.zhibo.zhiboimserver.domain.ChatMessage;
import com.zhibo.zhiboimserver.domain.ChatMessageType;
import com.zhibo.zhiboimserver.domain.Member;
import com.zhibo.zhiboimserver.service.ChatMessageService;
import com.zhibo.zhiboimserver.service.ChatService;
import com.zhibo.zhiboimserver.service.MemberService;
import com.zhibo.zhiboimserver.utils.JWTUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ServerEndpoint("/websocket/member")
public class WebSocketMemberController {

    private static ChatService chatService;

    private static ChatMessageService chatMessageService;

    private static MemberService userService;

    @Autowired
    private void setUserService(MemberService userService) {
        WebSocketMemberController.userService = userService;
    }

    @Autowired
    private void setChatService(ChatService chatService) {
        WebSocketMemberController.chatService = chatService;
    }

    @Autowired
    private void setChatMessageService(ChatMessageService chatMessageService) {
        WebSocketMemberController.chatMessageService = chatMessageService;
    }

    private Integer hostId;
    private Integer mcnId;
    private Integer anchorId;
    private int memberType;
    private int type;

    /**
     * 建立连接，用户上线
     * @param session
     */
    @OnOpen
    public void OnOpen(Session session) {
        String token = session.getRequestParameterMap().get("token").get(0);
        int type = Integer.valueOf(session.getRequestParameterMap().get("type").get(0));
        Integer memberId = JWTUtil.getMemberId(token);
        if (memberId == null) {
            log.error("can not get memberId from token:{}", token);
            return;
        }
        Member member = userService.getMember(memberId);
        if (member == null) {
            log.error("can not get member by memberId={}", memberId);
            return;
        }
        this.hostId = memberId;
        if (member.getType() == ChatMessageType.ANCHOR) {
            this.anchorId = memberId;
            this.mcnId = Integer.parseInt(session.getRequestParameterMap().get("mcnId").get(0));
        } else if (member.getType() == ChatMessageType.MCN) {
            this.mcnId = memberId;
            this.anchorId = Integer.parseInt(session.getRequestParameterMap().get("anchorId").get(0));
        }
        if (type == ChatMessageType.MCN) {
            if (!chatService.verifyMcnChat(this.anchorId, this.mcnId)) {
                try {
                    session.close();
                } catch (IOException e) {
                    log.error("close session failed", e);
                }
                return;
            }
        } else {
            chatService.createChatIfNotExist(type, this.anchorId, this.mcnId, this.hostId);
        }
        this.memberType = member.getType();
        this.type = type;
        chatService.memberOnline(this.anchorId, this.mcnId, this.memberType, session);
        log.info("[WebSocket] 连接成功，当前memberId={}", hostId);
    }

    @OnClose
    public void OnClose() {
        chatService.memberOffline(this.anchorId, this.mcnId, this.memberType);
        log.info("[WebSocket] 退出成功，当前memberId={}", hostId);
    }

    @OnMessage
    public void OnMessage(String message) {
        log.info("[WebSocket] userSendMessage：{}", message);
        ChatMessage chatMessage = JSONObject.parseObject(message, ChatMessage.class);
        chatMessage.setSenderId(this.hostId);
        chatMessage.setAnchorId(this.anchorId);
        chatMessage.setMcnId(this.mcnId);
        chatMessage.setSenderType(this.memberType);
        chatMessage.setType(this.type);
        chatMessageService.memberSendMessage(chatMessage);
    }
}
