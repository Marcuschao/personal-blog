package com.blog.personalblogbackend.controller;

import com.blog.personalblogbackend.support.Result;
import com.blog.personalblogbackend.constant.BlogSiteKeys;
import com.blog.personalblogbackend.dto.site.PublicSiteConfigDto;
import com.blog.personalblogbackend.service.SiteKvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/site")
public class SitePublicController {

    @Autowired
    private SiteKvService siteKvService;

    @GetMapping("/public-config")
    public Result<PublicSiteConfigDto> publicConfig() {
        String mode = siteKvService.get(BlogSiteKeys.CHATBOT_VISIBILITY).orElse("NONE");
        return Result.success(new PublicSiteConfigDto(mode));
    }
}
