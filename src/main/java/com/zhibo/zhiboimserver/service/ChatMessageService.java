package com.zhibo.zhiboimserver.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.websocket.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.zhibo.zhiboimserver.domain.Chat;
import com.zhibo.zhiboimserver.domain.ChatMessage;
import com.zhibo.zhiboimserver.domain.ChatMessageRequest;
import com.zhibo.zhiboimserver.mapper.ChatMapper;
import com.zhibo.zhiboimserver.mapper.ChatMessageMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ChatMessageService {

    @Autowired
    private ChatService chatService;

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    private ChatMapper chatMapper;

    public void insertChatMessage(ChatMessage chatMessage) {
        chatMessageMapper.insertChatMessage(chatMessage);
    }

    public List<ChatMessage> listChatMessage(ChatMessageRequest chatMessageRequest){
        List<ChatMessage> list =  chatMessageMapper.listChatMessage(chatMessageRequest);
        Collections.reverse(list);
        return list;
    }

    public void userSendMessage(ChatMessage chatMessage) {
        // 数据插入db
        chatMessageMapper.insertChatMessage(chatMessage);
        // 找到对应的member
        Session session = chatService.getMemberSession(chatMessage.getType(), chatMessage.getAnchorId(), chatMessage.getMcnId());
        if (session != null) {
            try {
                session.getBasicRemote().sendText(JSONObject.toJSONString(chatMessage));
            } catch (IOException e) {
                log.error("send message error", e);
            }
        } else {
            log.error("can not get member session{}", chatMessage);
        }
    }

    public void memberSendMessage(ChatMessage chatMessage) {

        Chat chat = chatMapper.getChat(chatMessage.getAnchorId(), chatMessage.getMcnId(), chatMessage.getType());
        if (chat == null) {
            // 应该已经创建了chat
            log.error("can not get chat,chatMessage is {}", chatMessage);
            return;
        }
        // 找到对应的user
        Integer userId = chatService.getUserByChatMessage(chatMessage);
        chatMessage.setUserId(userId == null ? 0 : userId);
        // 数据插入db
        chatMessageMapper.insertChatMessage(chatMessage);

        if (userId == null) {
            // 如果没有user，则unread+1
            chatMapper.increaseUnreadCount(chat);
            // 给所有在线的user发送广播
            sendChatMessageToAllOnlineUser(chatMessage);
        } else {
            // 如果有对应的user，则给user发送消息
            sendChatMessageToUser(chatMessage, userId);
        }
    }


    private void sendChatMessageToUser(ChatMessage chatMessage, Integer userId) {
        Session session = chatService.getUserSession(userId);
        if (session != null) {
            try {
                session.getBasicRemote().sendText(JSONObject.toJSONString(chatMessage));
            } catch (IOException e) {
                log.error("send message error", e);
            }
        } else {
            log.error("can not get user session");
        }
    }

    private void sendChatMessageToAllOnlineUser(ChatMessage chatMessage) {
        chatMessage.setBroadcast(true);
        Collection<Session> allUserSession = chatService.getAllOnlineUserSessions();
        if (allUserSession != null) {
            allUserSession.forEach((session) -> {
                try {
                    session.getBasicRemote().sendText(JSONObject.toJSONString(chatMessage));
                } catch (IOException e) {
                    log.error("send message error", e);
                }
            });
        }
    }
}
