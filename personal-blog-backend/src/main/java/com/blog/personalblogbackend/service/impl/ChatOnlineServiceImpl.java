package com.blog.personalblogbackend.service.impl;

import com.blog.personalblogbackend.config.websocket.ChatProperties;
import com.blog.personalblogbackend.model.vo.chat.OnlineUserVo;
import com.blog.personalblogbackend.service.ChatOnlineService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatOnlineServiceImpl implements ChatOnlineService {

    private static final String KEY_PREFIX = "chat:online:session:";
    private static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");

    private final StringRedisTemplate redis;
    private final ChatProperties chatProperties;
    private final ObjectMapper objectMapper;

    private ObjectMapper redisMapper() {
        return objectMapper.copy().registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public void markOnline(String sessionId, Long userId, String username, String avatar, boolean admin) {
        markOnline(sessionId, userId, username, avatar, admin, null);
    }

    @Override
    public void markOnline(String sessionId, Long userId, String username, String avatar, boolean admin, String ip) {
        if (!StringUtils.hasText(sessionId) || userId == null || username == null) {
            return;
        }
        if (isHttpSession(sessionId)) {
            if (hasActiveWsSession(userId)) {
                return;
            }
        } else {
            redis.delete(key("http:" + userId));
        }
        OnlineUserVo existing = readSession(sessionId);
        OnlineUserVo vo = new OnlineUserVo();
        vo.setUserId(userId);
        vo.setUsername(username);
        vo.setAvatar(avatar);
        vo.setAdmin(admin);
        vo.setSessionId(sessionId);
        vo.setIp(StringUtils.hasText(ip) ? ip : existing != null ? existing.getIp() : null);
        vo.setOnlineAt(existing != null && existing.getOnlineAt() != null ? existing.getOnlineAt() : LocalDateTime.now(ZONE));
        try {
            String json = redisMapper().writeValueAsString(vo);
            redis.opsForValue().set(key(sessionId), json, Duration.ofSeconds(chatProperties.getOnlineTtlSeconds()));
        } catch (Exception ex) {
            log.warn("markOnline failed sessionId={} userId={}", sessionId, userId, ex);
        }
    }

    @Override
    public void markOffline(String sessionId) {
        if (!StringUtils.hasText(sessionId)) {
            return;
        }
        redis.delete(key(sessionId));
    }

    @Override
    public void markOfflineByUserId(Long userId) {
        if (userId == null) {
            return;
        }
        redis.delete(key("http:" + userId));
        Set<String> keys = redis.keys(KEY_PREFIX + "*");
        if (keys == null || keys.isEmpty()) {
            return;
        }
        for (String redisKey : keys) {
            String json = redis.opsForValue().get(redisKey);
            if (json == null) {
                continue;
            }
            try {
                OnlineUserVo vo = redisMapper().readValue(json, new TypeReference<>() {
                });
                if (vo != null && userId.equals(vo.getUserId())) {
                    redis.delete(redisKey);
                }
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public List<OnlineUserVo> listOnlineUsers() {
        Set<String> keys = redis.keys(KEY_PREFIX + "*");
        if (keys == null || keys.isEmpty()) {
            return List.of();
        }
        Map<Long, OnlineUserVo> dedup = new LinkedHashMap<>();
        for (String redisKey : keys) {
            String json = redis.opsForValue().get(redisKey);
            if (json == null) {
                continue;
            }
            try {
                OnlineUserVo vo = redisMapper().readValue(json, new TypeReference<>() {
                });
                if (vo != null && vo.getUserId() != null) {
                    if (!StringUtils.hasText(vo.getSessionId())) {
                        vo.setSessionId(redisKey.substring(KEY_PREFIX.length()));
                    }
                    putOnlineUser(dedup, vo);
                }
            } catch (Exception ignored) {
            }
        }
        List<OnlineUserVo> users = new ArrayList<>(dedup.values());
        users.sort(Comparator.comparing(OnlineUserVo::getUsername, Comparator.nullsLast(String::compareToIgnoreCase)));
        return users;
    }

    @Override
    public List<OnlineUserVo> listOnlineSessions() {
        Set<String> keys = redis.keys(KEY_PREFIX + "*");
        if (keys == null || keys.isEmpty()) {
            return List.of();
        }
        List<OnlineUserVo> sessions = new ArrayList<>();
        for (String redisKey : keys) {
            String json = redis.opsForValue().get(redisKey);
            if (json == null) {
                continue;
            }
            try {
                OnlineUserVo vo = redisMapper().readValue(json, new TypeReference<>() {
                });
                if (vo != null) {
                    if (!StringUtils.hasText(vo.getSessionId())) {
                        vo.setSessionId(redisKey.substring(KEY_PREFIX.length()));
                    }
                    sessions.add(vo);
                }
            } catch (Exception ignored) {
            }
        }
        sessions.sort(Comparator.comparing(OnlineUserVo::getOnlineAt, Comparator.nullsLast(LocalDateTime::compareTo)).reversed());
        return filterHttpFallbackSessions(sessions);
    }

    private List<OnlineUserVo> filterHttpFallbackSessions(List<OnlineUserVo> sessions) {
        Set<Long> wsUsers = new java.util.HashSet<>();
        for (OnlineUserVo vo : sessions) {
            if (vo != null && vo.getUserId() != null && isWsSession(vo.getSessionId())) {
                wsUsers.add(vo.getUserId());
            }
        }
        if (wsUsers.isEmpty()) {
            return sessions;
        }
        List<OnlineUserVo> filtered = new ArrayList<>();
        for (OnlineUserVo vo : sessions) {
            if (vo == null) {
                continue;
            }
            if (isHttpSession(vo.getSessionId()) && wsUsers.contains(vo.getUserId())) {
                continue;
            }
            filtered.add(vo);
        }
        return filtered;
    }

    private boolean hasActiveWsSession(Long userId) {
        Set<String> keys = redis.keys(KEY_PREFIX + "*");
        if (keys == null || keys.isEmpty()) {
            return false;
        }
        for (String redisKey : keys) {
            if (redisKey.endsWith("http:" + userId)) {
                continue;
            }
            String json = redis.opsForValue().get(redisKey);
            if (json == null) {
                continue;
            }
            try {
                OnlineUserVo vo = redisMapper().readValue(json, new TypeReference<>() {
                });
                if (vo != null && userId.equals(vo.getUserId()) && isWsSession(vo.getSessionId())) {
                    return true;
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    private OnlineUserVo readSession(String sessionId) {
        String json = redis.opsForValue().get(key(sessionId));
        if (!StringUtils.hasText(json)) {
            return null;
        }
        try {
            return redisMapper().readValue(json, new TypeReference<>() {
            });
        } catch (Exception ex) {
            return null;
        }
    }

    private void putOnlineUser(Map<Long, OnlineUserVo> dedup, OnlineUserVo vo) {
        OnlineUserVo existing = dedup.get(vo.getUserId());
        if (existing == null || preferSession(vo, existing)) {
            dedup.put(vo.getUserId(), vo);
        }
    }

    private boolean preferSession(OnlineUserVo candidate, OnlineUserVo current) {
        boolean candidateWs = isWsSession(candidate.getSessionId());
        boolean currentWs = isWsSession(current.getSessionId());
        if (candidateWs && !currentWs) {
            return true;
        }
        if (!candidateWs && currentWs) {
            return false;
        }
        return existingOnlineAt(candidate).isAfter(existingOnlineAt(current));
    }

    private LocalDateTime existingOnlineAt(OnlineUserVo vo) {
        return vo.getOnlineAt() != null ? vo.getOnlineAt() : LocalDateTime.MIN;
    }

    private boolean isWsSession(String sessionId) {
        return StringUtils.hasText(sessionId) && !isHttpSession(sessionId);
    }

    private boolean isHttpSession(String sessionId) {
        return StringUtils.hasText(sessionId) && sessionId.startsWith("http:");
    }

    private String key(String sessionId) {
        return KEY_PREFIX + sessionId;
    }
}
