package com.blog.personalblogbackend.service;

import com.blog.personalblogbackend.model.dto.chat.ChatSendRequest;
import com.blog.personalblogbackend.model.vo.chat.ChatMessageVo;

public interface ChatReliabilityService {
    ChatMessageVo send(Long userId, String username, String avatar, boolean admin, ChatSendRequest request);

    void drainOfflineMessages(Long userId);

    void trackPresence(Long userId);
}
