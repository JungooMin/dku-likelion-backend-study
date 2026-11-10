package com.ll.demo03.domain.article.article.controller;

import com.ll.demo03.domain.article.article.entity.Article;
import com.ll.demo03.domain.article.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @GetMapping("/article/list")
    @ResponseBody
    public String showList(){
        StringBuilder sb = new StringBuilder();

        List<Article> articles = articleService.findAll();

        sb.append("<h1>게시물 목록</h1>\n");
        sb.append("<ul>\n");

        for (Article article : articles){
            sb.append("<li>");
            sb.append(article.getId());
            sb.append(" | ");
            sb.append(article.getCreateDate().toString().substring(2,10));
            sb.append(" | ");
            sb.append(article.getModifyDate().toString().substring(2,10));
            sb.append(" | ");
            sb.append(article.getAuthor().getNickname());
            sb.append(" | ");
            sb.append(article.getTitle());
            sb.append("</li>\n");
        }
        sb.append("</ul>");

        return sb.toString();
    }
}
