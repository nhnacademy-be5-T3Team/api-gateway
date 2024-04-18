package com.t3t.apigateway.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

/**
 * 토큰 재발급시 사용되는 refresh token의 entity
 * @author joohyun1996(이주현)
 */
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@RedisHash(value = "refresh", timeToLive = 1800)
public class Refresh {
    @Id
    private String token;
    @Indexed
    private String uuid;
}
