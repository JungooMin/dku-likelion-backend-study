package com.ll.demo03.domain.member.member.controller;

import com.ll.demo03.domain.auth.auth.service.AuthTokenService;
import com.ll.demo03.domain.member.member.dto.MemberDto;
import com.ll.demo03.domain.member.member.entity.Member;
import com.ll.demo03.domain.member.member.service.MemberService;
import com.ll.demo03.global.app.AppConfig;
import com.ll.demo03.global.exceptions.GlobalException;
import com.ll.demo03.global.rq.Rq;
import com.ll.demo03.global.rsData.RsData;
import com.ll.demo03.standard.dto.Empty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "ApiMemberController", description = "회원 CRUD 컨트롤러")
public class ApiV1MemberController {
    private final MemberService memberService;
    private final AuthTokenService authTokenService;
    private final Rq rq;

    @PostMapping("")
    @Transactional
    @Operation(summary = "회원가입")
    public RsData<MemberJoinRespBody> join(
            @RequestBody @Valid MemberJoinReqBody reqBody
    ) {
        RsData<Member> joinRs = memberService.join(reqBody.username, reqBody.password, reqBody.nickname);

        return joinRs.newDataOf(
                new MemberJoinRespBody(
                        new MemberDto(
                                joinRs.getData()
                        )
                )
        );
    }

    @PostMapping("/login")
    @Transactional
    @Operation(summary = "로그인", description = "성공하면 accessToken, refreshToken 쿠키가 생성됨")
    public RsData<MemberLoginRespBody> login(
            @RequestBody @Valid MemberLoginReqBody reqBody
    ) {
        Member member = memberService
                .findByUsername(reqBody.username)
                .orElseThrow(() -> new GlobalException("401-1", "해당 회원이 존재하지 않습니다."));

        if (!memberService.matchPassword(reqBody.password, member.getPassword())) {
            throw new GlobalException("401-2", "비밀번호가 일치하지 않습니다.");
        }

        String accessToken = authTokenService.genToken(member, AppConfig.getAccessTokenExpirationSec());
        rq.setCookie("accessToken", accessToken);
        rq.setCookie("refreshToken", member.getRefreshToken());

        return RsData.of(
                "200-1",
                "로그인 되었습니다.",
                new MemberLoginRespBody(
                        new MemberDto(member)
                )
        );
    }

    @GetMapping("/me")
    @Transactional
    @Operation(summary = "내 정보", description = "현재 로그인한 회원의 정보")
    public RsData<MemberMeRespBody> getMe() {
        return RsData.of(
                new MemberMeRespBody(
                        new MemberDto(rq.getMember())
                )
        );
    }

    @DeleteMapping("/logout")
    @Transactional
    @Operation(summary = "로그아웃")
    public RsData<Empty> logout() {
        rq.removeCookie("accessToken");
        rq.removeCookie("refreshToken");

        return RsData.OK;
    }

    @AllArgsConstructor
    @Getter
    public static class MemberJoinReqBody {
        @NotBlank
        private String username;

        @NotBlank
        private String password;

        @NotBlank
        private String nickname;
    }

    @AllArgsConstructor
    @Getter
    public static class MemberJoinRespBody {
        @NonNull
        MemberDto item;
    }

    @AllArgsConstructor
    @Getter
    public static class MemberLoginReqBody {
        @NotBlank
        private String username;

        @NotBlank
        private String password;
    }

    @AllArgsConstructor
    @Getter
    public static class MemberLoginRespBody {
        @NonNull
        MemberDto item;
    }

    @AllArgsConstructor
    @Getter
    public static class MemberMeRespBody {
        @NonNull
        MemberDto item;
    }
}
