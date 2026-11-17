package com.ll.demo03.domain.surl.surl.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ll.demo03.domain.member.member.entity.Member;
import com.ll.demo03.domain.surl.surl.entity.Surl;
import com.ll.demo03.global.jpa.entity.BaseTime;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
@Getter
public class SurlDto {
        private long id;
        private LocalDateTime createDate;
        private LocalDateTime modifyDate;
        private long authorId;
        private String authorName;
        private String body;
        private String url;
        private long count;

        public SurlDto(Surl surl){
            this.id = surl.getId();
            this.createDate = surl.getCreateDate();
            this.modifyDate = surl.getModifyDate();
            this.authorId = surl.getAuthor().getId();
            this.authorName = surl.getAuthor().getName();
            this.body = surl.getBody();
            this.url = surl.getUrl();
            this.count = surl.getCount();
        }
}
