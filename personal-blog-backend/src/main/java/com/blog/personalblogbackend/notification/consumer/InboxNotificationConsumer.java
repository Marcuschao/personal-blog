package com.blog.personalblogbackend.notification.consumer;

import com.blog.personalblogbackend.config.properties.NotificationRabbitProperties;
import com.blog.personalblogbackend.notification.NotificationMessage;
import com.blog.personalblogbackend.service.UserNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "blog.notification.enabled", havingValue = "true", matchIfMissing = true)
public class InboxNotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(InboxNotificationConsumer.class);

    private final UserNotificationService userNotificationService;

    public InboxNotificationConsumer(UserNotificationService userNotificationService) {
        this.userNotificationService = userNotificationService;
    }

    @RabbitListener(queues = "#{notificationRabbitProperties.inboxQueue}")
    public void onMessage(NotificationMessage message) {
        try {
            userNotificationService.saveFromMessage(message);
        } catch (Exception ex) {
            log.warn("[notification] inbox consume failed: {}", ex.toString());
            throw ex;
        }
    }
}
