package com.zhibo.zhiboimserver.controller;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zhibo.zhiboimserver.domain.Chat;
import com.zhibo.zhiboimserver.domain.ChatMessage;
import com.zhibo.zhiboimserver.domain.ChatMessageType;
import com.zhibo.zhiboimserver.service.ChatMessageService;
import com.zhibo.zhiboimserver.service.ChatService;

@Controller
public class TestController {

    @Autowired
    private ChatMessageService chatMessageService;

    @RequestMapping("/testInsert")
    @ResponseBody
    public String testInsert() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setUserId(3);
        chatMessage.setAnchorId(1);
        chatMessage.setMcnId(2);
        chatMessage.setSenderId(1);
        chatMessage.setMessage("test message");
        chatMessageService.insertChatMessage(chatMessage);
        return "success" + chatMessage.getId();
    }
}
