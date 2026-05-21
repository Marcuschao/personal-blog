package com.blog.personalblogbackend.stream.handler;

import com.blog.personalblogbackend.service.AuditLogQueryService;
import com.blog.personalblogbackend.stream.DomainEvent;
import com.blog.personalblogbackend.stream.DomainEventHandler;
import com.blog.personalblogbackend.stream.DomainEventType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(30)
public class OperationLogHandler implements DomainEventHandler {

    private final AuditLogQueryService auditLogQueryService;
    private final ObjectMapper objectMapper;

    public OperationLogHandler(AuditLogQueryService auditLogQueryService, ObjectMapper objectMapper) {
        this.auditLogQueryService = auditLogQueryService;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(DomainEventType type) {
        return true;
    }

    @Override
    public void handle(DomainEvent event) throws Exception {
        String action = "EVENT_" + event.getType().name();
        String detail = objectMapper.writeValueAsString(event.getPayload());
        auditLogQueryService.record("system", action, detail, null);
    }
}
