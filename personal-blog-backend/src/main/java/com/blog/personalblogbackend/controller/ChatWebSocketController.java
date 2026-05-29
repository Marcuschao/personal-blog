package com.blog.personalblogbackend.controller;

import com.blog.personalblogbackend.config.websocket.StompSessionAttributes;
import com.blog.personalblogbackend.model.dto.chat.ChatSendRequest;
import com.blog.personalblogbackend.service.ChatRecallService;
import com.blog.personalblogbackend.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;
    private final ChatRecallService chatRecallService;

    @MessageMapping("/chat")
    public void send(ChatSendRequest request, SimpMessageHeaderAccessor headerAccessor, Principal principal) {
        Map<String, Object> attrs = headerAccessor.getSessionAttributes();
        Long userId = attrs != null ? (Long) attrs.get(StompSessionAttributes.USER_ID) : null;
        String username = attrs != null ? (String) attrs.get(StompSessionAttributes.USERNAME) : null;
        String avatar = attrs != null ? (String) attrs.get(StompSessionAttributes.AVATAR) : null;
        String role = attrs != null ? (String) attrs.get(StompSessionAttributes.ROLE) : null;
        if (principal == null || userId == null) {
            throw new IllegalStateException("未登录");
        }
        boolean admin = "ADMIN".equals(role);
        chatService.send(userId, username, avatar, admin, request);
    }

    @MessageMapping("/chat/recall")
    public void recall(Map<String, Long> body, SimpMessageHeaderAccessor headerAccessor, Principal principal) {
        Map<String, Object> attrs = headerAccessor.getSessionAttributes();
        Long userId = attrs != null ? (Long) attrs.get(StompSessionAttributes.USER_ID) : null;
        String role = attrs != null ? (String) attrs.get(StompSessionAttributes.ROLE) : null;
        if (principal == null || userId == null || body == null || body.get("messageId") == null) {
            throw new IllegalStateException("未登录");
        }
        chatRecallService.recall(body.get("messageId"), userId, "ADMIN".equals(role));
    }
}
