package com.ll.demo03.domain.article.article.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Builder;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Builder
public class Article {
    @Id
    @GeneratedValue(strategy=IDENTITY)
    private Long id;
    private String title;
    private String body;
}
