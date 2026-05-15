package com.blog.personalblogbackend.controller;

import com.blog.personalblogbackend.audit.Audit;
import com.blog.personalblogbackend.support.Result;
import com.blog.personalblogbackend.constant.BlogSiteKeys;
import com.blog.personalblogbackend.dto.site.ChatbotVisibilityBody;
import com.blog.personalblogbackend.dto.site.PublicSiteConfigDto;
import com.blog.personalblogbackend.service.SiteKvService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/site")
public class AdminSiteController {

    @Autowired
    private SiteKvService siteKvService;

    @GetMapping("/public-config")
    public Result<PublicSiteConfigDto> getConfig() {
        String mode = siteKvService.get(BlogSiteKeys.CHATBOT_VISIBILITY).orElse("NONE");
        return Result.success(new PublicSiteConfigDto(mode));
    }

    @Audit("SITE_CHATBOT_VISIBILITY")
    @PutMapping("/chatbot-visibility")
    public Result<Void> setChatbotVisibility(@Valid @RequestBody ChatbotVisibilityBody body) {
        siteKvService.put(BlogSiteKeys.CHATBOT_VISIBILITY, body.getMode());
        return Result.success(null);
    }
}
