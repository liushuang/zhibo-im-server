package com.zhibo.zhiboimserver.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import com.zhibo.zhiboimserver.domain.Member;

@Mapper
@Component
public interface MemberMapper {
    @Select("select id, head_img_url as headImgUrl, username, type from member where id = #{id}")
    Member getMemberById(Integer id);

    @Update("update mcn set intent_number = intent_number - 1 where member_id = #{memberId} and intent_number > 0")
    int decreaseIntentNumber(Integer memberId);
}
