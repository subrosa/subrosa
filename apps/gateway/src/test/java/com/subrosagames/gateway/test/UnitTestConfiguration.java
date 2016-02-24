package com.subrosagames.gateway.test;

import java.io.IOException;
import javax.annotation.PreDestroy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.Protocol;
import redis.embedded.RedisServer;

/**
 * Mocks and bean overrides for unit tests.
 */
@Configuration
@Profile("unit-test")
public class UnitTestConfiguration {

    private RedisServer redisServer;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() throws IOException {
        redisServer = new RedisServer(Protocol.DEFAULT_PORT);
        redisServer.start();
        return new JedisConnectionFactory(new JedisShardInfo("localhost", Protocol.DEFAULT_PORT));
    }

    @PreDestroy
    public void destroy() {
        redisServer.stop();
    }

}
