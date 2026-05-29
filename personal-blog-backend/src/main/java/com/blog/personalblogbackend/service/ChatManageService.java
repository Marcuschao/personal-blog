package com.blog.personalblogbackend.service;

import com.blog.personalblogbackend.common.support.PageResult;
import com.blog.personalblogbackend.model.entity.ChatMessage;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatManageService {
    PageResult<ChatMessage> page(long page, long size, String username, String keyword,
                                 LocalDateTime start, LocalDateTime end);

    void deleteOne(Long id);

    void deleteBatch(List<Long> ids);
}
