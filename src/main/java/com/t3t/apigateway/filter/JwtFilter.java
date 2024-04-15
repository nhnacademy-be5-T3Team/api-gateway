package com.t3t.apigateway.filter;

import com.t3t.apigateway.common.JwtUtils;
import com.t3t.apigateway.exception.BlackListTokenExceptions;
import com.t3t.apigateway.exception.TokenExpiredExceptions;
import com.t3t.apigateway.exception.TokenNotAuthenticatedExceptions;
import com.t3t.apigateway.exception.TokenNotExistExceptions;
import com.t3t.apigateway.service.TokenService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtFilter implements GatewayFilter {
    private final JwtUtils jwtUtils;
    private final TokenService tokenService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String url = exchange.getRequest().getURI().getPath();
        String access = null;

        chain.filter(exchange);
        if (!url.startsWith("/at")) {
        }
        if (Objects.isNull(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))){
            throw new TokenNotExistExceptions("Access Token Not Exists");
        }

        access = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION).trim().split(" ")[1];

        if (Objects.isNull(access)) {
            throw new TokenNotExistExceptions("Access Token Not Exists!");
        }
        if (jwtUtils.getValidation(access)) {
            throw new TokenNotAuthenticatedExceptions("Not Authenticated Token");
        }
        if (tokenService.findBlackList(access)){
            throw new BlackListTokenExceptions("Token Already Expired");
        }
        try {
            if (jwtUtils.isExpired(access) == false) {
                // 토큰의 만료시간이 5분 이하인 경우 refresh로 자동 재전송
                if (jwtUtils.checkReIssue(access) && !tokenService.getRefreshByUUID(jwtUtils.getUUID(access)).getToken().isEmpty()) {
                    ServerHttpRequest request = exchange.getRequest().mutate() // exchange.mutate()를 사용해서 요청, 응답 변경
                            .path("/refresh")
                            .build();
                    return chain.filter(exchange.mutate().request(request).build());
                }
            }
        } catch (ExpiredJwtException e) {
            // access, refresh 둘다 만료된 경우
            if (!tokenService.refreshTokenExists(e.getClaims().get("uuid", String.class))){
                throw new TokenExpiredExceptions("Cannot Reissue Access Token");
            }
            // 토큰이 만료되었는데 Refresh토큰이 있는 경우
            if (Objects.nonNull(tokenService.getRefreshByUUID(e.getClaims().get("uuid", String.class)))) {
                ServerHttpRequest request = exchange.getRequest().mutate()
                        .path("/refresh")
                        .build();
                return chain.filter(exchange.mutate().request(request).build());
            } else {
                // 토큰이 만료되었는데 Refresh 토큰도 만료된 경우
                throw new TokenExpiredExceptions("Token Expired!");
            }
        }
        // 경로 재작성 Logic
        // "/secrets/*/*"중 최하위 url만 전달
        if (url.startsWith("/at")){
            String[] strs = url.split("/");
            String newPath = "/" + strs[strs.length-1];
            ServerHttpRequest newRequest = exchange.getRequest().mutate().path(newPath).build();
            return chain.filter(exchange.mutate().request(newRequest).build());
        }

        // 모든 과정들을 만족하지 않는 경우
        // mono.defer()? Project Reactor에서 제공하는 메소드. 각 구독자에게 Mono 제공
        return chain.filter(exchange).then(Mono.defer(() -> {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            DataBuffer buffer = response.bufferFactory().wrap("Not Match Any Conditions.".getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        }));
    }
}
