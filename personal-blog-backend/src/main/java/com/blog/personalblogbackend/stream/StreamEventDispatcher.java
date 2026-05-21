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
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class StreamEventDispatcher implements StreamListener<String, MapRecord<String, String, String>> {

    private static final Logger log = LoggerFactory.getLogger(StreamEventDispatcher.class);

    private final StreamProperties streamProperties;
    private final RedisTemplate<String, String> streamRedisTemplate;
    private final ObjectMapper objectMapper;
    private final EventPublisher eventPublisher;
    private final List<DomainEventHandler> handlers;

    public StreamEventDispatcher(StreamProperties streamProperties,
                                 RedisTemplate<String, String> streamRedisTemplate,
                                 ObjectMapper objectMapper,
                                 EventPublisher eventPublisher,
                                 List<DomainEventHandler> handlers) {
        this.streamProperties = streamProperties;
        this.streamRedisTemplate = streamRedisTemplate;
        this.objectMapper = objectMapper;
        this.eventPublisher = eventPublisher;
        this.handlers = handlers;
    }

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        if (!streamProperties.isEnabled()) {
            return;
        }
        Map<String, String> value = message.getValue();
        String eventJson = value.get("event");
        int retryCount = parseRetryCount(value.get("retryCount"));
        RecordId recordId = message.getId();
        String streamKey = streamProperties.getStreamKey();
        String group = streamProperties.getConsumerGroup();

        try {
            DomainEvent event = objectMapper.readValue(eventJson, DomainEvent.class);
            for (DomainEventHandler handler : handlers) {
                if (handler.supports(event.getType())) {
                    handler.handle(event);
                }
            }
            streamRedisTemplate.opsForStream().acknowledge(streamKey, group, recordId);
        } catch (Exception ex) {
            log.warn("[stream] consume failed id={} retry={}: {}", recordId, retryCount, ex.toString());
            handleFailure(streamKey, group, recordId, eventJson, retryCount, ex);
        }
    }

    private void handleFailure(String streamKey, String group, RecordId recordId,
                               String eventJson, int retryCount, Exception ex) {
        try {
            streamRedisTemplate.opsForStream().acknowledge(streamKey, group, recordId);
            if (retryCount < streamProperties.getMaxRetries()) {
                DomainEvent event = objectMapper.readValue(eventJson, DomainEvent.class);
                if (streamProperties.getRetryBackoffMs() > 0) {
                    Thread.sleep(streamProperties.getRetryBackoffMs());
                }
                eventPublisher.publish(event, retryCount + 1);
            } else {
                Map<String, String> dead = new HashMap<>();
                dead.put("event", eventJson);
                dead.put("error", ex.getMessage() != null ? ex.getMessage() : ex.toString());
                dead.put("originalId", recordId.getValue());
                dead.put("retryCount", String.valueOf(retryCount));
                StreamOperations<String, String, String> ops = streamRedisTemplate.opsForStream();
                MapRecord<String, String, String> record = StreamRecords.mapBacked(dead)
                        .withStreamKey(streamProperties.getDeadStreamKey());
                ops.add(record, XAddOptions.maxlen(streamProperties.getMaxStreamLength()).approximateTrimming(true));
            }
        } catch (Exception e) {
            log.error("[stream] failure handling error id={}: {}", recordId, e.toString());
        }
    }

    private static int parseRetryCount(String raw) {
        if (raw == null || raw.isBlank()) {
            return 0;
        }
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
