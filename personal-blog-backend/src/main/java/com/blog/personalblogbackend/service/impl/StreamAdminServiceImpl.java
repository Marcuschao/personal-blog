package com.blog.personalblogbackend.service.impl;

import com.blog.personalblogbackend.config.properties.StreamProperties;
import com.blog.personalblogbackend.model.vo.stream.DeadLetterVo;
import com.blog.personalblogbackend.model.dto.stream.StreamPendingResponse;
import com.blog.personalblogbackend.common.exception.ServiceException;
import com.blog.personalblogbackend.service.StreamAdminService;
import com.blog.personalblogbackend.stream.DomainEvent;
import com.blog.personalblogbackend.stream.EventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.Limit;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.PendingMessages;
import org.springframework.data.redis.connection.stream.PendingMessagesSummary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StreamAdminServiceImpl implements StreamAdminService {

    private final StreamProperties streamProperties;
    private final RedisTemplate<String, String> streamRedisTemplate;
    private final EventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    public StreamAdminServiceImpl(StreamProperties streamProperties,
                                  RedisTemplate<String, String> streamRedisTemplate,
                                  EventPublisher eventPublisher,
                                  ObjectMapper objectMapper) {
        this.streamProperties = streamProperties;
        this.streamRedisTemplate = streamRedisTemplate;
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
    }

    @Override
    public StreamPendingResponse pendingSummary() {
        if (!streamProperties.isEnabled()) {
            return new StreamPendingResponse(false, 0, null);
        }
        PendingMessagesSummary summary = streamRedisTemplate.opsForStream().pending(
                streamProperties.getStreamKey(),
                streamProperties.getConsumerGroup());
        long total = summary != null ? summary.getTotalPendingMessages() : 0;
        Long oldestIdle = null;
        if (summary != null && summary.getTotalPendingMessages() > 0) {
            PendingMessages pending = streamRedisTemplate.opsForStream().pending(
                    streamProperties.getStreamKey(),
                    streamProperties.getConsumerGroup(),
                    Range.unbounded(),
                    1);
            if (pending != null && !pending.isEmpty()) {
                oldestIdle = pending.iterator().next().getElapsedTimeSinceLastDelivery().toMillis();
            }
        }
        return new StreamPendingResponse(true, total, oldestIdle);
    }

    @Override
    public List<DeadLetterVo> listDeadLetters(int limit) {
        if (!streamProperties.isEnabled()) {
            return List.of();
        }
        int n = limit > 0 ? Math.min(limit, 200) : 50;
        List<MapRecord<String, Object, Object>> rows = streamRedisTemplate.opsForStream().reverseRange(
                streamProperties.getDeadStreamKey(),
                Range.unbounded(),
                Limit.limit().count(n));
        List<DeadLetterVo> out = new ArrayList<>();
        if (rows == null) {
            return out;
        }
        for (MapRecord<String, Object, Object> row : rows) {
            Map<String, String> v = toStringMap(row.getValue());
            DeadLetterVo vo = new DeadLetterVo();
            vo.setRecordId(row.getId().getValue());
            vo.setEventJson(v.get("event"));
            vo.setError(v.get("error"));
            vo.setOriginalId(v.get("originalId"));
            String rc = v.get("retryCount");
            vo.setRetryCount(rc != null ? Integer.parseInt(rc) : 0);
            out.add(vo);
        }
        return out;
    }

    @Override
    public void retryDeadLetter(String recordId) {
        if (!streamProperties.isEnabled()) {
            throw new ServiceException(400, "Stream 未启用");
        }
        if (recordId == null || recordId.isBlank()) {
            throw new ServiceException(400, "recordId 不能为空");
        }
        List<MapRecord<String, Object, Object>> rows = streamRedisTemplate.opsForStream().range(
                streamProperties.getDeadStreamKey(),
                Range.closed(recordId, recordId));
        if (rows == null || rows.isEmpty()) {
            throw new ServiceException(404, "死信消息不存在");
        }
        MapRecord<String, Object, Object> row = rows.get(0);
        String eventJson = toStringMap(row.getValue()).get("event");
        if (eventJson == null || eventJson.isBlank()) {
            throw new ServiceException(400, "死信消息体无效");
        }
        try {
            DomainEvent event = objectMapper.readValue(eventJson, DomainEvent.class);
            eventPublisher.publish(event, 0);
            streamRedisTemplate.opsForStream().delete(streamProperties.getDeadStreamKey(), row.getId());
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException(500, "重新投递失败");
        }
    }

    private static Map<String, String> toStringMap(Map<Object, Object> raw) {
        Map<String, String> out = new HashMap<>();
        if (raw == null) {
            return out;
        }
        raw.forEach((k, v) -> out.put(String.valueOf(k), v != null ? String.valueOf(v) : null));
        return out;
    }
}
