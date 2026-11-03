package com.ll.demo03.domain.surl.surl.controller;

import com.ll.demo03.domain.auth.auth.service.AuthService;
import com.ll.demo03.domain.member.member.entity.Member;
import com.ll.demo03.domain.surl.surl.dto.SurlDto;
import com.ll.demo03.domain.surl.surl.entity.Surl;
import com.ll.demo03.domain.surl.surl.service.SurlService;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/surls")
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "ApiSurlController", description = "Surl CRUD 컨트롤러")
public class ApiV1SurlController {
    private final SurlService surlService;
    private final AuthService authService;
    private final Rq rq;

    @PostMapping("")
    @Transactional
    @Operation(summary = "생성")
    public RsData<SurlAddRespBody> add(
            @RequestBody @Valid SurlAddReqBody reqBody
    ) {
        Member member = rq.getMember(); // 현재 브라우저로 로그인한 회원

        RsData<Surl> addRs = surlService.add(member, reqBody.body, reqBody.url);

        return addRs.newDataOf(
                new SurlAddRespBody(
                        new SurlDto(addRs.getData())
                )
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "단건조회")
    public RsData<SurlGetRespBody> get(
            @PathVariable long id
    ) {
        Surl surl = surlService.findById(id).orElseThrow(GlobalException.E404::new);

        authService.checkCanGetSurl(rq.getMember(), surl);

        return RsData.of(
                new SurlGetRespBody(
                        new SurlDto(surl)
                )
        );
    }

    @GetMapping("")
    @Operation(summary = "다건조회")
    public RsData<SurlGetItemsRespBody> getItems() {
        Member member = rq.getMember();

        List<Surl> surls = surlService.findByAuthorOrderByIdDesc(member);

        return RsData.of(
                new SurlGetItemsRespBody(
                        surls.stream()
                                .map(SurlDto::new)
                                .toList()
                )
        );
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "삭제")
    public RsData<Empty> delete(
            @PathVariable long id
    ) {
        Surl surl = surlService.findById(id).orElseThrow(GlobalException.E404::new);

        authService.checkCanDeleteSurl(rq.getMember(), surl);

        surlService.delete(surl);

        return RsData.OK;
    }

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "수정")
    public RsData<SurlModifyRespBody> modify(
            @PathVariable long id,
            @RequestBody @Valid SurlModifyReqBody reqBody
    ) {
        Surl surl = surlService.findById(id).orElseThrow(GlobalException.E404::new);

        authService.checkCanModifySurl(rq.getMember(), surl);

        RsData<Surl> modifyRs = surlService.modify(surl, reqBody.body, reqBody.url);

        return modifyRs.newDataOf(
                new SurlModifyRespBody(
                        new SurlDto(modifyRs.getData())
                )
        );
    }

    @AllArgsConstructor
    @Getter
    public static class SurlAddReqBody {
        private String body;

        @NotBlank
        private String url;
    }

    @AllArgsConstructor
    @Getter
    public static class SurlAddRespBody {
        @NonNull
        private SurlDto item;
    }

    @AllArgsConstructor
    @Getter
    public static class SurlGetRespBody {
        @NonNull
        private SurlDto item;
    }

    @AllArgsConstructor
    @Getter
    public static class SurlGetItemsRespBody {
        @NonNull
        private List<SurlDto> items;
    }

    @AllArgsConstructor
    @Getter
    public static class SurlModifyReqBody {
        private String body;

        @NotBlank
        private String url;
    }

    @AllArgsConstructor
    @Getter
    public static class SurlModifyRespBody {
        @NonNull
        private SurlDto item;
    }
}