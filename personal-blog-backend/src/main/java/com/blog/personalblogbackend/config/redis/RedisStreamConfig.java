package com.blog.personalblogbackend.config.redis;

import com.blog.personalblogbackend.config.properties.StreamProperties;
import com.blog.personalblogbackend.stream.StreamEventDispatcher;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

import java.time.Duration;

@Configuration
@ConditionalOnProperty(name = "blog.stream.enabled", havingValue = "true")
public class RedisStreamConfig {

    private static final Logger log = LoggerFactory.getLogger(RedisStreamConfig.class);

    private final StreamProperties streamProperties;
    private final RedisConnectionFactory connectionFactory;
    private final RedisTemplate<String, String> streamRedisTemplate;
    private final StreamEventDispatcher dispatcher;

    public RedisStreamConfig(StreamProperties streamProperties,
                             RedisConnectionFactory connectionFactory,
                             RedisTemplate<String, String> streamRedisTemplate,
                             StreamEventDispatcher dispatcher) {
        this.streamProperties = streamProperties;
        this.connectionFactory = connectionFactory;
        this.streamRedisTemplate = streamRedisTemplate;
        this.dispatcher = dispatcher;
    }

    @PostConstruct
    void ensureConsumerGroup() {
        try {
            streamRedisTemplate.opsForStream().createGroup(
                    streamProperties.getStreamKey(),
                    ReadOffset.latest(),
                    streamProperties.getConsumerGroup());
        } catch (Exception e) {
            log.debug("[stream] consumer group init: {}", e.getMessage());
        }
    }

    @Bean
    StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer() {
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options =
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                        .pollTimeout(Duration.ofSeconds(2))
                        .batchSize(1)
                        .build();
        StreamMessageListenerContainer<String, MapRecord<String, String, String>> container =
                StreamMessageListenerContainer.create(connectionFactory, options);
        Consumer consumer = Consumer.from(streamProperties.getConsumerGroup(), streamProperties.getConsumerName());
        StreamOffset<String> offset = StreamOffset.create(streamProperties.getStreamKey(), ReadOffset.lastConsumed());
        container.receive(consumer, offset, dispatcher);
        container.start();
        return container;
    }
}
