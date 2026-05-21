package com.blog.personalblogbackend.service;

import com.blog.personalblogbackend.model.vo.stream.DeadLetterVo;
import com.blog.personalblogbackend.model.dto.stream.StreamPendingResponse;

import java.util.List;

public interface StreamAdminService {

    StreamPendingResponse pendingSummary();

    List<DeadLetterVo> listDeadLetters(int limit);

    void retryDeadLetter(String recordId);
}
