package com.ll.demo03.domain.member.member.controller;

import com.ll.demo03.domain.member.member.entity.Member;
import com.ll.demo03.domain.member.member.service.MemberService;
import com.ll.demo03.global.exceptions.GlobalException;
import com.ll.demo03.global.rsData.RsData;
import com.ll.demo03.standard.dto.stil.Ut;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class ApiV1MemberController {
    private final MemberService memberService;

    @AllArgsConstructor
    @Getter
    public static class MemberJoinReqBody{
        @NotBlank(message = "아이디를 입력해주세요.")
        private String username;
        @NotBlank(message = "비밀번호를 입력해주세요.")
        private String password;
        @NotBlank(message = "닉네임을 입력해주세요.")
        private String nickname;
    }

    @AllArgsConstructor
    @Getter
    public static class MemberJoinRespBody{
        Member item;
    }

    @PostMapping("")
    public RsData<Member> join(
            @RequestBody @Valid MemberJoinReqBody reqBody
    ){
        RsData<Member> joinRs = memberService.join(reqBody.username, reqBody.password, reqBody.nickname);
        Member member = joinRs.getData();

        return RsData.of(new MemberJoinRespBody(joinRs.getData()).getItem());
    }
}
