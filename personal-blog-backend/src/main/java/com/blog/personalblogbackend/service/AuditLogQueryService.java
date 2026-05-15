package com.blog.personalblogbackend.service;

import com.blog.personalblogbackend.support.PageResult;
import com.blog.personalblogbackend.dto.audit.AuditLogVo;

public interface AuditLogQueryService {

    PageResult<AuditLogVo> page(long current, long size);

    void record(String username, String action, String detail, String ip);
}
