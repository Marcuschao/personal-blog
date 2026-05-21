package com.blog.personalblogbackend.stream;

import com.blog.personalblogbackend.config.properties.StreamProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.connection.RedisStreamCommands.XAddOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.HashMap;
import java.util.Map;

@Service
public class EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(EventPublisher.class);

    private final StreamProperties streamProperties;
    private final RedisTemplate<String, String> streamRedisTemplate;
    private final ObjectMapper objectMapper;

    public EventPublisher(StreamProperties streamProperties,
                          RedisTemplate<String, String> streamRedisTemplate,
                          ObjectMapper objectMapper) {
        this.streamProperties = streamProperties;
        this.streamRedisTemplate = streamRedisTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishAfterCommit(DomainEvent event) {
        if (!streamProperties.isEnabled() || event == null) {
            return;
        }
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    publish(event, 0);
                }
            });
        } else {
            publish(event, 0);
        }
    }

    public RecordId publish(DomainEvent event, int retryCount) {
        if (!streamProperties.isEnabled() || event == null) {
            return null;
        }
        try {
            String json = objectMapper.writeValueAsString(event);
            Map<String, String> body = new HashMap<>();
            body.put("event", json);
            body.put("retryCount", String.valueOf(retryCount));
            StreamOperations<String, String, String> ops = streamRedisTemplate.opsForStream();
            MapRecord<String, String, String> record = StreamRecords.mapBacked(body)
                    .withStreamKey(streamProperties.getStreamKey());
            XAddOptions options = XAddOptions.maxlen(streamProperties.getMaxStreamLength())
                    .approximateTrimming(true);
            return ops.add(record, options);
        } catch (Exception e) {
            log.warn("[stream] publish failed: {}", e.toString());
            return null;
        }
    }
}
