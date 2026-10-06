package com.ll.demo03.global.initData;

import com.ll.demo03.domain.article.article.entity.Article;
import com.ll.demo03.domain.article.article.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("!prod") //!prod == dev or test
@Configuration
@RequiredArgsConstructor
public class NotProd {
    private final ArticleRepository articleRepository;
    @Bean
    public ApplicationRunner initNotProd(){
        return args -> {
            System.out.println("NotProd.initNotProd1");
            System.out.println("NotProd.initNotProd2");
            System.out.println("NotProd.initNotProd3");

            articleRepository.save(
                    Article.builder()
                            .title("제목")
                            .body("내용")
                            .build()
            );
        };
    }
}
