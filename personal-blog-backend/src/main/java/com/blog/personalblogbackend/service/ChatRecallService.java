package com.blog.personalblogbackend.service;

public interface ChatRecallService {
    void recall(Long messageId, Long operatorId, boolean admin);
}
