package com.zhibo.zhiboimserver.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import com.zhibo.zhiboimserver.domain.Chat;
import com.zhibo.zhiboimserver.domain.ChatWithUserInfo;

@Component
@Mapper
public interface ChatMapper {
    @Insert("insert into chat(anchor_id, mcn_id, user_id, starter_id, type, unread_count, picked, pick_time, created_time, updated_time) "
            + "values(#{anchorId}, #{mcnId}, #{userId}, #{starterId}, #{type}, #{unreadCount}, #{picked}, #{pickTime}, now(), now())")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertChat(Chat chat);

    @Update("update chat set user_id = #{userId}, unread_count = #{unreadCount}, picked = #{picked}, pick_time = #{pickTime}, updated_time = now()"
            + "where anchor_id = #{anchorId} and mcn_id = #{mcnId} and type=#{type}")
    int updateChat(Chat chat);

    @Update("update chat set picked = 0 where user_id = #{userId}")
    int setAllUserToUnpick(Integer userId);

    @Select("select id, anchor_id as anchorId, mcn_id as mcnId, user_id as userId, starter_id as starter_id, type, unread_count as unreadCount, "
            + "picked, pick_time as pickTime, created_time as createdTime, updated_time as updatedTime "
            + "from chat where anchor_id = #{anchorId} and mcn_id = #{mcnId} and type = #{type}")
    Chat getChat(Integer anchorId, Integer mcnId, int type);

    @Update("update chat set unread_count = unread_count +1, updated_time = now()"
            + "where anchor_id = #{anchorId} and mcn_id = #{mcnId} and type=#{type}")
    void increaseUnreadCount(Chat chat);

    @Select("select chat.id, chat.anchor_id as anchorId , chat.mcn_id as mcnId, chat.user_id as userId, chat.starter_id as starterId , chat.type, "
            + " chat.unread_count as unreadCount, chat.picked, chat.pick_time as pickTime, chat.created_time as createdTime , "
            + " chat.updated_time as updatedTime, member.username, member.head_img_url as headImgUrl "
            + " from chat "
            + " left join member on chat.starter_id = member.id "
            + " where chat.unread_count > 0 order by chat.updated_time desc ")
    List<ChatWithUserInfo> getUnreadChatList();
}