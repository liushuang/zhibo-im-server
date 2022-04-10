package com.zhibo.zhiboimserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zhibo.zhiboimserver.domain.Member;
import com.zhibo.zhiboimserver.mapper.MemberMapper;

@Service
public class MemberService {

    @Autowired
    private MemberMapper memberMapper;

    public Member getMember(Integer memberId){
        return memberMapper.getMemberById(memberId);
    }
}
