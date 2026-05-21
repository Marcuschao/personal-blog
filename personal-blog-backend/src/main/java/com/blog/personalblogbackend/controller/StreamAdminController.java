package com.blog.personalblogbackend.controller;

import com.blog.personalblogbackend.model.vo.stream.DeadLetterVo;
import com.blog.personalblogbackend.model.dto.stream.StreamDeadRetryRequest;
import com.blog.personalblogbackend.model.dto.stream.StreamPendingResponse;
import com.blog.personalblogbackend.service.StreamAdminService;
import com.blog.personalblogbackend.common.support.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/stream")
public class StreamAdminController {

    private final StreamAdminService streamAdminService;

    public StreamAdminController(StreamAdminService streamAdminService) {
        this.streamAdminService = streamAdminService;
    }

    @GetMapping("/pending")
    public Result<StreamPendingResponse> pending() {
        return Result.success(streamAdminService.pendingSummary());
    }

    @GetMapping("/dead")
    public Result<List<DeadLetterVo>> dead(@RequestParam(defaultValue = "50") int limit) {
        return Result.success(streamAdminService.listDeadLetters(limit));
    }

    @PostMapping("/dead/retry")
    public Result<Void> retryDead(@RequestBody StreamDeadRetryRequest body) {
        streamAdminService.retryDeadLetter(body != null ? body.getRecordId() : null);
        return Result.success(null);
    }
}
