package com.ll.demo03.domain.home.home.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Transactional(readOnly = true)
@Tag(name = "HomeController", description = "홈 컨트롤러")
public class HomeController {
    @Value("${custom.site.name}")
    private String siteName;

    @GetMapping("/")
    @ResponseBody
    @Operation(summary = "API 메인화면")
    public String showMain() {
        return "Hello, World!, on " + siteName;
    }
}
