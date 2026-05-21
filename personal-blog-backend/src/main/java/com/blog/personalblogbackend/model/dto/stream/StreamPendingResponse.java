package com.blog.personalblogbackend.model.dto.stream;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StreamPendingResponse {
    private boolean enabled;
    private long pendingCount;
    private Long oldestIdleMs;
}
