package com.blog.personalblogbackend.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "blog.stream")
public class StreamProperties {
    private boolean enabled = true;
    private String streamKey = "blog:events";
    private String deadStreamKey = "blog:events:dead";
    private String consumerGroup = "blog-consumer-group";
    private String consumerName = "blog-dispatcher-1";
    private int maxRetries = 3;
    private long retryBackoffMs = 10000;
    private long maxStreamLength = 1000;
}
