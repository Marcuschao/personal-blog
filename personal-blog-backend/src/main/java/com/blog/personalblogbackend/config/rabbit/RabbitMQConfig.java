package com.blog.personalblogbackend.config.rabbit;

import com.blog.personalblogbackend.config.properties.NotificationRabbitProperties;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;

@Configuration
@EnableRabbit
@ConditionalOnProperty(name = "blog.notification.enabled", havingValue = "true", matchIfMissing = true)
public class RabbitMQConfig {

    @Bean
    public TopicExchange notificationExchange(NotificationRabbitProperties props) {
        return new TopicExchange(props.getExchange(), true, false);
    }

    @Bean
    public Queue inboxQueue(NotificationRabbitProperties props) {
        return QueueBuilder.durable(props.getInboxQueue()).build();
    }

    @Bean
    public Queue pushQueue(NotificationRabbitProperties props) {
        return QueueBuilder.durable(props.getPushQueue()).build();
    }

    @Bean
    public Queue mailQueue(NotificationRabbitProperties props) {
        return QueueBuilder.durable(props.getMailQueue()).build();
    }

    @Bean
    public Queue auditQueue(NotificationRabbitProperties props) {
        return QueueBuilder.durable(props.getAuditQueue()).build();
    }

    @Bean
    public Binding bindLike(Queue inboxQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(inboxQueue).to(notificationExchange).with(NotificationRabbitProperties.RK_LIKE);
    }

    @Bean
    public Binding bindFavorite(Queue inboxQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(inboxQueue).to(notificationExchange).with(NotificationRabbitProperties.RK_FAVORITE);
    }

    @Bean
    public Binding bindComment(Queue inboxQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(inboxQueue).to(notificationExchange).with(NotificationRabbitProperties.RK_COMMENT);
    }

    @Bean
    public Binding bindFollow(Queue inboxQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(inboxQueue).to(notificationExchange).with(NotificationRabbitProperties.RK_FOLLOW);
    }

    @Bean
    public Binding bindPush(Queue pushQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(pushQueue).to(notificationExchange).with(NotificationRabbitProperties.RK_ARTICLE_PUBLISHED);
    }

    @Bean
    public Binding bindMail(Queue mailQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(mailQueue).to(notificationExchange).with(NotificationRabbitProperties.RK_ARTICLE_PUBLISHED);
    }

    @Bean
    public Binding bindAudit(Queue auditQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(auditQueue).to(notificationExchange).with("notification.event.#");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
