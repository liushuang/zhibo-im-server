package com.zhibo.zhiboimserver.controller;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zhibo.zhiboimserver.domain.ChatMessage;
import com.zhibo.zhiboimserver.domain.ChatMessageRequest;
import com.zhibo.zhiboimserver.domain.ChatMessageType;
import com.zhibo.zhiboimserver.domain.Member;
import com.zhibo.zhiboimserver.domain.Result;
import com.zhibo.zhiboimserver.service.ChatMessageService;
import com.zhibo.zhiboimserver.service.MemberService;
import com.zhibo.zhiboimserver.utils.JWTUtil;

@Controller
@RequestMapping("/api/chat")
public class ChatMessageController {

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private MemberService memberService;

    @RequestMapping("/user/message")
    @ResponseBody
    public Result<List<ChatMessage>> userGetChatMessage(@RequestBody ChatMessageRequest chatMessageRequest) {
        Integer userId = JWTUtil.getUserId(chatMessageRequest.getToken());
        if (userId == null) {
            return new Result<>(null, Result.CODE_400);
        }
        return new Result<>(chatMessageService.listChatMessage(chatMessageRequest));
    }

    @RequestMapping("/member/message")
    @ResponseBody
    public Result<List<ChatMessage>> memberGetChatMessage(@RequestBody ChatMessageRequest chatMessageRequest) {
        Integer memberId = JWTUtil.getMemberId(chatMessageRequest.getToken());
        if (memberId == null) {
            return new Result<>(null, Result.CODE_400);
        }
        Member member = memberService.getMember(memberId);
        if (member == null) {
            return new Result<>(null, Result.CODE_400);
        }
        if (member.getType() == ChatMessageType.ANCHOR) {
            chatMessageRequest.setAnchorId(memberId);
            if (chatMessageRequest.getType() == ChatMessageType.LAW) {
                chatMessageRequest.setMcnId(0);
            }
        } else if (member.getType() == ChatMessageType.MCN) {
            chatMessageRequest.setMcnId(memberId);
            if (chatMessageRequest.getType() == ChatMessageType.LAW) {
                chatMessageRequest.setAnchorId(0);
            }
        }
        return new Result<>(chatMessageService.listChatMessage(chatMessageRequest));
    }
}
