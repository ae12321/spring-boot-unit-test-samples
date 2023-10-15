package com.example.demo.sample;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import redis.clients.jedis.Jedis;

public class RedisTest {

    private static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:3.0.6"))
        .withExposedPorts(6379);

    @BeforeEach
    public void beforeEach() {
        redis.start();
    }

    @AfterEach
    public void afterEach() {
        redis.stop();
    }

    @Test
    public void aaa() {
        System.out.println("host: " + redis.getHost());
        System.out.println("port: " + redis.getExposedPorts());
        Assertions.assertThat("asdf" + "asdf").isEqualTo("asdfasdf");

        Jedis jedis = new Jedis(redis.getHost(), redis.getMappedPort(6379), false);

        jedis.set("key1", "valuevalue");

        String getted = jedis.get("key1");
        jedis.close();
        System.out.println("jedis get: " + getted);

        Assertions.assertThat(getted).isEqualTo("valuevalue");
    }
}
