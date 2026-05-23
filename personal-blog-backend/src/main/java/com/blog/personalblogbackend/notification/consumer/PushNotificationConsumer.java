package com.blog.personalblogbackend.notification.consumer;

import com.blog.personalblogbackend.notification.NotificationMessage;
import com.blog.personalblogbackend.service.WebPushService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConditionalOnProperty(name = "blog.notification.enabled", havingValue = "true", matchIfMissing = true)
public class PushNotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(PushNotificationConsumer.class);

    private final WebPushService webPushService;

    public PushNotificationConsumer(WebPushService webPushService) {
        this.webPushService = webPushService;
    }

    @RabbitListener(queues = "#{notificationRabbitProperties.pushQueue}")
    public void onMessage(NotificationMessage message) {
        try {
            Map<String, Object> p = message.getPayload();
            if (p == null) {
                return;
            }
            Long articleId = toLong(p.get("articleId"));
            String title = p.get("title") != null ? String.valueOf(p.get("title")) : null;
            if (articleId != null) {
                webPushService.notifyNewArticle(articleId, title);
            }
        } catch (Exception ex) {
            log.warn("[notification] push consume failed: {}", ex.toString());
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
