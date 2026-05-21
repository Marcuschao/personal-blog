package com.blog.personalblogbackend.stream.handler;

import com.blog.personalblogbackend.service.WebPushService;
import com.blog.personalblogbackend.stream.DomainEvent;
import com.blog.personalblogbackend.stream.DomainEventHandler;
import com.blog.personalblogbackend.stream.DomainEventType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Order(10)
public class PushNotificationHandler implements DomainEventHandler {

    private final WebPushService webPushService;

    public PushNotificationHandler(WebPushService webPushService) {
        this.webPushService = webPushService;
    }

    @Override
    public boolean supports(DomainEventType type) {
        return type == DomainEventType.ARTICLE_PUBLISHED;
    }

    @Override
    public void handle(DomainEvent event) {
        Map<String, Object> p = event.getPayload();
        Long articleId = toLong(p.get("articleId"));
        String title = p.get("title") != null ? String.valueOf(p.get("title")) : null;
        if (articleId != null) {
            webPushService.notifyNewArticle(articleId, title);
        }
    }

    private static Long toLong(Object v) {
        if (v == null) return null;
        if (v instanceof Number n) return n.longValue();
        return Long.parseLong(String.valueOf(v));
    }
}
