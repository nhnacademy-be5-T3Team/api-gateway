package com.t3t.apigateway.property;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedisProperties {
    private String host;
    private Integer port;
    private Integer database;
    private String password;
}
