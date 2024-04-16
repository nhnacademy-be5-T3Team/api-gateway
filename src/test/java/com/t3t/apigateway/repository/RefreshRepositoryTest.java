package com.t3t.apigateway.repository;

import com.t3t.apigateway.entity.Refresh;
import com.t3t.apigateway.exception.TokenNotExistExceptions;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@DataRedisTest
class RefreshRepositoryTest {

    @Autowired
    private RefreshRepository refreshRepository;
    private Refresh refresh;

    @BeforeEach
    public void setUp(){
        refresh = Refresh.builder()
                .token("r.r.r")
                .uuid("1")
                .build();

        refreshRepository.save(refresh);
    }

    @AfterEach
    public void tearDown(){
        refreshRepository.delete(refresh);
    }

    @Test
    void findByUuid() {
        String uuid = "1";
        Refresh newRefresh = refreshRepository.findByUuid(uuid).get();
        Assertions.assertThat(newRefresh.getUuid()).isEqualTo("1");
    }

    @Test
    void findByUuidFailed(){
        String uuid = "2";
        Boolean result = refreshRepository.findByUuid(uuid).isPresent();
        Assertions.assertThat(result).isFalse();
    }
}