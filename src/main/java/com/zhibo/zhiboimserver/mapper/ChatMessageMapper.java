package com.zhibo.zhiboimserver.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import com.zhibo.zhiboimserver.domain.ChatMessage;
import com.zhibo.zhiboimserver.domain.ChatMessageRequest;

@Component
@Mapper
public interface ChatMessageMapper {

    @Insert("insert into chat_message(anchor_id, mcn_id, user_id, message, type, sender_id, sender_type, created_time) values "
            + "(#{anchorId}, #{mcnId}, #{userId}, #{message}, #{type}, #{senderId}, #{senderType}, now())")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertChatMessage(ChatMessage chatMessage);

    @Select("select id, anchor_id as anchorId, mcn_id as mcnId, user_id as userId, message, type, sender_type as senderType, sender_id as senderId, "
            + "created_time as createdTime from chat_message "
            + "where type=#{type} and anchor_id = #{anchorId} and mcn_id = #{mcnId} and id < #{offset} "
            + "order by id desc limit #{pageSize}")
    List<ChatMessage> listChatMessage(ChatMessageRequest chatMessageRequest);
}
