package com.blog.personalblogbackend.stream;

public interface DomainEventHandler {
    boolean supports(DomainEventType type);

    void handle(DomainEvent event) throws Exception;
}
