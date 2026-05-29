package com.blog.personalblogbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.personalblogbackend.common.exception.ServiceException;
import com.blog.personalblogbackend.common.support.PageResult;
import com.blog.personalblogbackend.mapper.ChatMessageMapper;
import com.blog.personalblogbackend.model.entity.ChatMessage;
import com.blog.personalblogbackend.service.ChatManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatManageServiceImpl implements ChatManageService {

    private final ChatMessageMapper chatMessageMapper;

    @Override
    public PageResult<ChatMessage> page(long page, long size, String username, String keyword,
                                        LocalDateTime start, LocalDateTime end) {
        long pg = Math.max(page, 1);
        long sz = Math.min(Math.max(size, 1), 100);
        LambdaQueryWrapper<ChatMessage> q = new LambdaQueryWrapper<ChatMessage>()
                .orderByDesc(ChatMessage::getId);
        if (StringUtils.hasText(username)) {
            q.like(ChatMessage::getUsername, username.trim());
        }
        if (StringUtils.hasText(keyword)) {
            q.like(ChatMessage::getContent, keyword.trim());
        }
        if (start != null) {
            q.ge(ChatMessage::getCreateTime, start);
        }
        if (end != null) {
            q.le(ChatMessage::getCreateTime, end);
        }
        Page<ChatMessage> result = chatMessageMapper.selectPage(new Page<>(pg, sz), q);
        return PageResult.build(result.getRecords(), result.getTotal(), result.getSize(), result.getCurrent());
    }

    @Override
    public void deleteOne(Long id) {
        if (id == null) {
            throw new ServiceException(400, "ID 无效");
        }
        chatMessageMapper.deleteById(id);
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new ServiceException(400, "请选择消息");
        }
        chatMessageMapper.deleteBatchIds(ids);
    }
}
