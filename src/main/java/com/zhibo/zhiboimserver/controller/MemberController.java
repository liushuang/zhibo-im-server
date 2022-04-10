package com.zhibo.zhiboimserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zhibo.zhiboimserver.domain.Member;
import com.zhibo.zhiboimserver.domain.Result;
import com.zhibo.zhiboimserver.service.MemberService;
import com.zhibo.zhiboimserver.utils.JWTUtil;

@Controller
@RequestMapping("/api/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @RequestMapping("/currentInfo")
    @ResponseBody
    public Result<Member> getCurrentMemberInfo(@RequestParam("token") String token) {
        Integer memberId = JWTUtil.getMemberId(token);
        if (memberId == null) {
            return new Result(null, Result.CODE_400);
        }
        return new Result(memberService.getMember(memberId));
    }

    @RequestMapping("/info/{memberId}")
    @ResponseBody
    public Result<Member> getMemberInfo(@PathVariable("memberId") Integer memberId) {
        return new Result(memberService.getMember(memberId));
    }

}
