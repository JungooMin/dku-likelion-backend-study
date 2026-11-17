package com.ll.demo03.domain.surl.surl.repository;

import com.ll.demo03.domain.member.member.entity.Member;
import com.ll.demo03.domain.surl.surl.entity.Surl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SurlRepository extends JpaRepository<Surl, Long> {

    List<Surl> findByAuthorOrderByIdDesc(Member author);
}