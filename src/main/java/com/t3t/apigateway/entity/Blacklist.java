package com.t3t.apigateway.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

/**
 * logout된 토큰을 redis에서 관리하기 위한 entity
 * @author joohyun1996(이주현)
 */
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@RedisHash(value = "BlackList")
public class Blacklist {
    @Id
    private String blackList;
}
