package com.blog.personalblogbackend.service.impl;

import com.blog.personalblogbackend.config.properties.NotificationRabbitProperties;
import com.blog.personalblogbackend.model.dto.notification.NotificationMqStatusDto;
import com.blog.personalblogbackend.service.NotificationAdminService;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationAdminServiceImpl implements NotificationAdminService {

    private final NotificationRabbitProperties props;
    private final ConnectionFactory connectionFactory;

    public NotificationAdminServiceImpl(NotificationRabbitProperties props, ConnectionFactory connectionFactory) {
        this.props = props;
        this.connectionFactory = connectionFactory;
    }

    @Override
    public NotificationMqStatusDto status() {
        NotificationMqStatusDto dto = new NotificationMqStatusDto();
        dto.setEnabled(props.isEnabled());
        dto.setExchange(props.getExchange());
        dto.setQueues(List.of(
                props.getInboxQueue(),
                props.getPushQueue(),
                props.getMailQueue(),
                props.getAuditQueue()));
        dto.setConnected(isConnected());
        return dto;
    }

    private boolean isConnected() {
        if (!props.isEnabled()) {
            return false;
        }
        try {
            connectionFactory.createConnection().close();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
