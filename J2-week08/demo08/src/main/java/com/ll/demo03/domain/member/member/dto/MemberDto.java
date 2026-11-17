package com.ll.demo03.domain.member.member.dto;

import com.ll.demo03.domain.member.member.entity.Member;
import com.ll.demo03.domain.surl.surl.entity.Surl;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
@Getter
public class MemberDto {
        private long id;
        private LocalDateTime createDate;
        private LocalDateTime modifyDate;
        private String username;
        private String nickname;


        public MemberDto(Member member){
            this.id = member.getId();
            this.createDate = member.getCreateDate();
            this.modifyDate = member.getModifyDate();
            this.username = member.getUsername();
            this.nickname = member.getNickname();
        }
}
