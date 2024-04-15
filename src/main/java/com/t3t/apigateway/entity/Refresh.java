package com.t3t.apigateway.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

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
