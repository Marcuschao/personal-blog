package com.blog.personalblogbackend.stream.handler;

import com.blog.personalblogbackend.config.properties.BlogSiteProperties;
import com.blog.personalblogbackend.service.BlogMailService;
import com.blog.personalblogbackend.service.SubscriberService;
import com.blog.personalblogbackend.stream.DomainEvent;
import com.blog.personalblogbackend.stream.DomainEventHandler;
import com.blog.personalblogbackend.stream.DomainEventType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Order(20)
public class MailNotificationHandler implements DomainEventHandler {

    private final SubscriberService subscriberService;
    private final BlogMailService blogMailService;
    private final BlogSiteProperties blogSiteProperties;

    public MailNotificationHandler(SubscriberService subscriberService,
                                     BlogMailService blogMailService,
                                     BlogSiteProperties blogSiteProperties) {
        this.subscriberService = subscriberService;
        this.blogMailService = blogMailService;
        this.blogSiteProperties = blogSiteProperties;
    }

    @Override
    public boolean supports(DomainEventType type) {
        return type == DomainEventType.ARTICLE_PUBLISHED;
    }

    @Override
    public void handle(DomainEvent event) {
        List<String> emails = subscriberService.listActiveEmails();
        if (emails.isEmpty()) {
            return;
        }
        Map<String, Object> p = event.getPayload();
        Long articleId = toLong(p.get("articleId"));
        String title = p.get("title") != null ? String.valueOf(p.get("title")) : "";
        String summary = p.get("summary") != null ? String.valueOf(p.get("summary")) : "";
        String url = blogSiteProperties.resolvePublicUrl("/article/" + articleId);
        String subject = "[" + blogSiteProperties.getSiteTitle() + "] 新文章：" + title;
        String body = title + "\n\n" + summary + "\n\n阅读：" + url + "\n";
        for (String email : emails) {
            blogMailService.sendIfConfigured(email, subject, body);
        }
    }

    private static Long toLong(Object v) {
        if (v == null) return null;
        if (v instanceof Number n) return n.longValue();
        return Long.parseLong(String.valueOf(v));
    }
}
