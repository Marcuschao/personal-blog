package com.blog.personalblogbackend.model.dto.push;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PushStatsResponse {
    private long subscriptionCount;
}
