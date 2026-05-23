package com.blog.personalblogbackend.notification.consumer;

import com.blog.personalblogbackend.config.properties.BlogSiteProperties;
import com.blog.personalblogbackend.notification.NotificationMessage;
import com.blog.personalblogbackend.service.BlogMailService;
import com.blog.personalblogbackend.service.SubscriberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "blog.notification.enabled", havingValue = "true", matchIfMissing = true)
public class MailNotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(MailNotificationConsumer.class);

    private final SubscriberService subscriberService;
    private final BlogMailService blogMailService;
    private final BlogSiteProperties blogSiteProperties;

    public MailNotificationConsumer(SubscriberService subscriberService,
                                    BlogMailService blogMailService,
                                    BlogSiteProperties blogSiteProperties) {
        this.subscriberService = subscriberService;
        this.blogMailService = blogMailService;
        this.blogSiteProperties = blogSiteProperties;
    }

    @RabbitListener(queues = "#{notificationRabbitProperties.mailQueue}")
    public void onMessage(NotificationMessage message) {
        try {
            List<String> emails = subscriberService.listActiveEmails();
            if (emails.isEmpty()) {
                return;
            }
            Map<String, Object> p = message.getPayload();
            if (p == null) {
                return;
            }
            Long articleId = toLong(p.get("articleId"));
            String title = p.get("title") != null ? String.valueOf(p.get("title")) : "";
            String summary = p.get("summary") != null ? String.valueOf(p.get("summary")) : "";
            if (articleId == null) {
                return;
            }
            String url = blogSiteProperties.resolvePublicUrl("/article/" + articleId);
            String subject = "[" + blogSiteProperties.getSiteTitle() + "] 新文章：" + title;
            String body = title + "\n\n" + summary + "\n\n阅读：" + url + "\n";
            for (String email : emails) {
                blogMailService.sendIfConfigured(email, subject, body);
            }
        } catch (Exception ex) {
            log.warn("[notification] mail consume failed: {}", ex.toString());
            throw ex;
        }
    }

    private static Long toLong(Object v) {
        if (v == null) {
            return null;
        }
        if (v instanceof Number n) {
            return n.longValue();
        }
        return Long.parseLong(String.valueOf(v));
    }
}
