package com.blog.personalblogbackend.controller;

import com.blog.personalblogbackend.config.properties.BlogSiteProperties;
import com.blog.personalblogbackend.model.dto.push.PushSubscribeRequest;
import com.blog.personalblogbackend.model.dto.push.PushUnsubscribeRequest;
import com.blog.personalblogbackend.model.dto.push.VapidPublicResponse;
import com.blog.personalblogbackend.config.security.CurrentUserService;
import com.blog.personalblogbackend.service.impl.PushSubscriptionServiceImpl;
import com.blog.personalblogbackend.common.support.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/push")
public class PushPublicController {

    private final BlogSiteProperties blogSiteProperties;
    private final PushSubscriptionServiceImpl pushSubscriptionService;
    private final CurrentUserService currentUserService;

    public PushPublicController(BlogSiteProperties blogSiteProperties,
                                PushSubscriptionServiceImpl pushSubscriptionService,
                                CurrentUserService currentUserService) {
        this.blogSiteProperties = blogSiteProperties;
        this.pushSubscriptionService = pushSubscriptionService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/vapid-public-key")
    public Result<VapidPublicResponse> vapidPublicKey() {
        BlogSiteProperties.PushSettings p = blogSiteProperties.getPush();
        return Result.success(new VapidPublicResponse(p.isEnabled(), p.getPublicKey(), p.getSubject()));
    }

    @PostMapping("/subscribe")
    public Result<Void> subscribe(@RequestBody PushSubscribeRequest body, HttpServletRequest request) {
        Long userId = currentUserService.optionalUserId();
        pushSubscriptionService.subscribe(body, userId, request);
        return Result.success(null);
    }

    @PostMapping("/unsubscribe")
    public Result<Void> unsubscribe(@RequestBody PushUnsubscribeRequest body) {
        pushSubscriptionService.unsubscribe(body != null ? body.getEndpoint() : null);
        return Result.success(null);
    }
}
