package com.ll.demo03.global.rq;

import com.ll.demo03.domain.member.member.entity.Member;
import com.ll.demo03.domain.member.member.service.MemberService;
import com.ll.demo03.global.exceptions.GlobalException;
import com.ll.demo03.standard.dto.stil.Ut;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
@RequiredArgsConstructor
public class Rq {
    private final HttpServletRequest req;
    private final HttpServletResponse resp;
    private final MemberService memberService;
    private Member member;

    public Member getMember(){
        if(member != null) return member;

        String actorUsername = req.getParameter("actorUsername");
        String actorPassword = req.getParameter("actorPassword");

        if(Ut.str.isBlank(actorUsername)) throw new GlobalException("401-1", "인증정보(아이디)를 입력해주세요.");
        if(Ut.str.isBlank(actorPassword)) throw new GlobalException("401-2", "인증정보(비밀번호)를 입력해주세요.");

        Member loginedMember = memberService.findByUsername(actorUsername).orElseThrow(()-> new GlobalException("401-3", "해당 회원이 존재하지 않습니다."));
        if(!loginedMember.getPassword().equals(actorPassword) == false) throw new GlobalException("401-4", "비밀번호가 일치하지 않습니다.");

        member = loginedMember;

        return loginedMember;
    }

    public String getCurrentUrlPath(){
        return req.getRequestURI();
    }

    public void setStatusCode(int statusCode){
        resp.setStatus(statusCode);
    }


}
