package com.zhibo.zhiboimserver.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zhibo.zhiboimserver.domain.Chat;
import com.zhibo.zhiboimserver.domain.ChatWithUserInfo;
import com.zhibo.zhiboimserver.domain.PickupChatRequest;
import com.zhibo.zhiboimserver.domain.Result;
import com.zhibo.zhiboimserver.service.ChatService;
import com.zhibo.zhiboimserver.utils.JWTUtil;

@Controller
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private ChatService chatService;

    /**
     * 用户接起来聊天
     * @param pickupChatRequest pickup请求
     */
    @RequestMapping("/pickup")
    @ResponseBody
    public Result<String> pickup(@RequestBody PickupChatRequest pickupChatRequest) {
        Chat chat = new Chat();
        chat.setUserId(JWTUtil.getUserId(pickupChatRequest.getToken()));
        chat.setMcnId(pickupChatRequest.getMcnId());
        chat.setAnchorId(pickupChatRequest.getAnchorId());
        chat.setType(pickupChatRequest.getType());
        chatService.pickupMemberChat(chat);
        return new Result<>("success");
    }

    /**
     * 用户挂断聊天
     * @param pickupChatRequest pickup请求
     */
    @RequestMapping("/hangup")
    @ResponseBody
    public Result<String> hangup(@RequestBody PickupChatRequest pickupChatRequest) {
        Chat chat = new Chat();
        chat.setUserId(JWTUtil.getUserId(pickupChatRequest.getToken()));
        chat.setMcnId(pickupChatRequest.getMcnId());
        chat.setAnchorId(pickupChatRequest.getAnchorId());
        chat.setType(pickupChatRequest.getType());
        chatService.hangupMemberChat(chat);
        return new Result<>("success");
    }

    /**
     * 获取所有包含未读消息的聊天
     * @param token 验证权限的token
     * @return
     */
    @RequestMapping("/unreadMessageList")
    @ResponseBody
    public Result<List<ChatWithUserInfo>> getUnreadMessageList(@RequestParam("token") String token) {
        if (JWTUtil.getUserId(token) == null) {
            return new Result(null, Result.CODE_400);
        }
        return new Result(chatService.getUnreadChatList());
    }
}
