package com.zhibo.zhiboimserver.service;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.websocket.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zhibo.zhiboimserver.domain.Chat;
import com.zhibo.zhiboimserver.domain.ChatMessage;
import com.zhibo.zhiboimserver.domain.ChatMessageType;
import com.zhibo.zhiboimserver.domain.ChatWithUserInfo;
import com.zhibo.zhiboimserver.mapper.ChatMapper;
import com.zhibo.zhiboimserver.mapper.MemberMapper;

@Service
public class ChatService {
    @Autowired
    private ChatMapper chatMapper;

    @Autowired
    private MemberMapper memberMapper;

    /**
     * 用来保存所有在线的userSession的Map，key为userId
     */
    private Map<Integer, Session> userSessionMap = new HashMap<>();

    private Map<String, Session> memberSessionMap = new HashMap<>();

    private Map<String, Integer> chatToUserMap = new HashMap<>();

    /**
     * 用户上线
     * @param session
     * @param userId 上线的userId
     */
    public void userOnline(Session session, Integer userId) {
        // 如果是顶号上线，需要先把用户下线
        if (userSessionMap.get(userId) != null) {
            userOffline(userId);
        }
        // 将用户session放入userSessionMap中
        userSessionMap.put(userId, session);
    }

    public void userOffline(Integer userId) {
        // 所有被这个用户pick的对话修改为unpick
        chatMapper.setAllUserToUnpick(userId);
        chatToUserMap.entrySet().removeIf(entry -> entry.getValue().equals(userId));
        // 将userSessionMap中对应的记录移除
        userSessionMap.remove(userId);
    }

    public void pickupMemberChat(Chat chat) {
        // DB中保存pickup的记录
        chat.setPicked(true);
        chat.setPickTime(Calendar.getInstance().getTime());
        chat.setUnreadCount(0);
        chatMapper.updateChat(chat);
        // 内存的Map中保存pickup的记录
        String memberSessionKey = getMemberSessionMapKey(chat);
        chatToUserMap.put(memberSessionKey, chat.getUserId());
    }

    public void hangupMemberChat(Chat chat) {
        // 更新DB中pickup的记录
        chat.setPicked(false);
        chat.setUnreadCount(0);
        chatMapper.updateChat(chat);
        // 内存的Map中删除pickup的记录，
        String memberSessionKey = getMemberSessionMapKey(chat);
        chatToUserMap.remove(memberSessionKey, chat.getUserId());
    }

    public List<ChatWithUserInfo> getUnreadChatList(){
        return chatMapper.getUnreadChatList();
    }

    private static String getMemberSessionMapKey(Integer anchorId, Integer mcnId, int type) {
        return String.format("%s_%d_%d", type, anchorId, mcnId);
    }

    private static String getMemberSessionMapKey(Chat chatMessage) {
        return String.format("%s_%d_%d", chatMessage.getType(), chatMessage.getAnchorId(), chatMessage.getMcnId());
    }

    private static String getMemberSessionMapKey(ChatMessage chatMessage) {
        return String.format("%s_%d_%d", chatMessage.getType(), chatMessage.getAnchorId(), chatMessage.getMcnId());
    }

    public void memberOffline(Integer anchorId, Integer mcnId, int type) {
        String memberSessionMapKey = getMemberSessionMapKey(anchorId, mcnId, type);
        memberSessionMap.remove(memberSessionMapKey);
    }

    public Integer getUserByChatMessage(ChatMessage chatMessage) {
        String memberSessionMapKey = getMemberSessionMapKey(chatMessage);
        return chatToUserMap.get(memberSessionMapKey);
    }

    public void memberOnline(Integer anchorId, Integer mcnId, int type, Session session) {
        String memberSessionMapKey = getMemberSessionMapKey(anchorId, mcnId, type);
        memberSessionMap.put(memberSessionMapKey, session);
    }

    public Session getUserSession(Integer userId) {
        return userSessionMap.get(userId);
    }

    public Collection<Session> getAllOnlineUserSessions() {
        return userSessionMap.values();
    }

    /**
     * 获取member对应的session，用于向member发送消息
     * @param type
     * @param anchorId
     * @param mcnId
     * @return
     */
    public Session getMemberSession(int type, Integer anchorId, Integer mcnId) {
        String memberSessionMapKey = getMemberSessionMapKey(anchorId, mcnId, type);
        return memberSessionMap.get(memberSessionMapKey);
    }

    /**
     * 检查由MCN发起的咨询，是否可以发起。
     *  1. 如果已经扣费过，则可以继续
     *  2. 如果没有扣费，则先尝试扣费并添加记录。如果扣费失败则不能发起
     * @param anchorId 主播id
     * @param mcnId mcnId
     * @return 不能发起时返回false
     */
    public boolean verifyMcnChat(Integer anchorId, Integer mcnId) {
        Chat chat = chatMapper.getChat(anchorId, mcnId, ChatMessageType.MCN);
        if(chat != null){
            return true;
        }
        int result = memberMapper.decreaseIntentNumber(mcnId);
        if(result ==0){
            return false;
        }
        chat = new Chat();
        chat.setAnchorId(anchorId);
        chat.setMcnId(mcnId);
        chat.setType(ChatMessageType.MCN);
        chat.setStarterId(mcnId);
        chatMapper.insertChat(chat);
        return true;
    }

    public void createChatIfNotExist(int type, Integer anchorId, Integer mcnId, int starterId) {
        Chat chat = chatMapper.getChat(anchorId, mcnId, type);
        if (chat == null) {
            chat = new Chat();
            chat.setAnchorId(anchorId);
            chat.setMcnId(mcnId);
            chat.setType(type);
            chat.setStarterId(starterId);
            chatMapper.insertChat(chat);
        }
    }
}
