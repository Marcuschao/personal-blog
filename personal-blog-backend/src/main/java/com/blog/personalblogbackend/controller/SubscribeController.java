package com.blog.personalblogbackend.controller;

import com.blog.personalblogbackend.config.properties.BlogSiteProperties;
import com.blog.personalblogbackend.common.support.Result;
import com.blog.personalblogbackend.model.dto.subscribe.SubscribeRequest;
import com.blog.personalblogbackend.service.SubscriberService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/api/subscribe")
public class SubscribeController {

    private final SubscriberService subscriberService;
    private final BlogSiteProperties blogSiteProperties;

    public SubscribeController(SubscriberService subscriberService, BlogSiteProperties blogSiteProperties) {
        this.subscriberService = subscriberService;
        this.blogSiteProperties = blogSiteProperties;
    }

    @PostMapping
    public Result<String> subscribe(@Valid @RequestBody SubscribeRequest body) {
        subscriberService.subscribe(body);
        return Result.success("若已开启邮件，将发送确认链接");
    }

    @GetMapping("/confirm")
    public RedirectView confirm(@RequestParam String token) {
        boolean ok = subscriberService.confirmSubscribe(token);
        String base = blogSiteProperties.getSiteUrl().replaceAll("/$", "");
        return new RedirectView(ok ? base + "?subscribe=confirmed" : base + "?subscribe=invalid");
    }
}
