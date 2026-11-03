package com.ll.demo03.global.initData;

import com.ll.demo03.domain.member.member.entity.Member;
import com.ll.demo03.domain.member.member.service.MemberService;
import com.ll.demo03.global.app.AppConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

// !prod == dev or test
@Configuration
@RequiredArgsConstructor
@Slf4j
public class All {
    private final MemberService memberService;
    @Lazy
    @Autowired
    private All self;

    @Bean
    @Order(3)
    public ApplicationRunner initAll() {
        return args -> {
            self.work1();
        };
    }

    @Transactional
    public void work1() {
        log.debug("initAll started");

        if (memberService.count() > 0) return;

        Member memberSystem = memberService.join("system", "1234", "시스템").getData();
        if (AppConfig.isNotProd()) memberSystem.setRefreshToken(memberSystem.getUsername());

        Member memberAdmin = memberService.join("admin", "1234", "관리자").getData();
        if (AppConfig.isNotProd()) memberAdmin.setRefreshToken(memberAdmin.getUsername());

        Member memberUser1 = memberService.join("user1", "1234", "유저 1").getData();
        if (AppConfig.isNotProd()) memberUser1.setRefreshToken(memberUser1.getUsername());

        Member memberUser2 = memberService.join("user2", "1234", "유저 2").getData();
        if (AppConfig.isNotProd()) memberUser2.setRefreshToken(memberUser2.getUsername());
    }
}
