package com.ll.demo03.domain.member.member.controller;

import com.ll.demo03.domain.member.member.dto.MemberDto;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
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
        MemberDto item;
    }

    @PostMapping("")
    @Transactional
    public RsData<MemberJoinRespBody> join(
            @RequestBody @Valid MemberJoinReqBody reqBody
    ){
        RsData<Member> joinRs = memberService.join(reqBody.username, reqBody.password, reqBody.nickname);
        Member member = joinRs.getData();

        return joinRs.newDataOf(
                new MemberJoinRespBody(
                        new MemberDto(
                                joinRs.getData()
                    )
                )
        );
    }
}
