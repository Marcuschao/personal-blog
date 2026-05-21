package com.blog.personalblogbackend.stream.handler;

import com.blog.personalblogbackend.stream.DomainEvent;
import com.blog.personalblogbackend.stream.DomainEventHandler;
import com.blog.personalblogbackend.stream.DomainEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(40)
public class SocialCardHandler implements DomainEventHandler {

    private static final Logger log = LoggerFactory.getLogger(SocialCardHandler.class);

    @Override
    public boolean supports(DomainEventType type) {
        return type == DomainEventType.ARTICLE_PUBLISHED;
    }

    @Override
    public void handle(DomainEvent event) {
        log.info("[stream] SocialCard placeholder articleId={}", event.getPayload().get("articleId"));
    }
}
