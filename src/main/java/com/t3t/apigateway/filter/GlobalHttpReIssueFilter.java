package com.t3t.apigateway.filter;

import com.t3t.apigateway.common.JwtUtils;
import com.t3t.apigateway.exception.TokenExpiredExceptions;
import com.t3t.apigateway.exception.TokenNotExistExceptions;
import com.t3t.apigateway.service.TokenService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Objects;

/**
 * JWT 토큰 재발급을 위한 Filter
 * 토큰 만료시간 5분전 혹은 만료시 Refresh토큰이 있으면 "/auth/refresh"로 요청 전송 
 * 요청받은 응답(Authorization)을 헤더에 넣고 기존 url로 요청 전송
 * @author joohyun1996(이주현)
 */
@RequiredArgsConstructor
@Component
public class GlobalHttpReIssueFilter implements GlobalFilter, Ordered {
    private final JwtUtils jwtUtils;
    private final TokenService tokenService;

    /**
     * 요청에 있는 HttpHeader.AUTHORIZATION값으로 필터링
     * @param exchange,chain
     * @author joohyun1996(이주현)
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String originalUrl = exchange.getRequest().getURI().getPath();
        // 요청에 토큰이 없는 경우는 pass
        if (Objects.isNull(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))) {
            return chain.filter(exchange);
        }
        String access = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION).trim().split(" ")[1];
        if (Objects.isNull(access)) {
            throw new TokenNotExistExceptions("No Such Token!");
        }
        try {
            // access token이 만료되지 않았다면
            if (!jwtUtils.isExpired(access)) {
                // 토큰 만료 시간이 5분 넘게 남은 경우는 pass
                if (!jwtUtils.checkReIssue(access)) {
                    return chain.filter(exchange);
                }

                // 토큰 만료 시간이 5분보다 적은 경우
                // Refresh 토큰이 만료된 경우
                // url 요청 통과, 재발급 X
                if (!tokenService.refreshTokenExists(jwtUtils.getUUID(access))) {
                    return chain.filter(exchange);
                } else {
                    // Refresh 토큰이 있는 경우 해당 url로 전송
                    WebClient webClient = WebClient.create();
                    Mono<ClientResponse> responseMono = webClient.post()
                            // 추후 profile별 url로 전송되게 해야함
                            .uri("http://localhost:8084/refresh")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + access)
                            .exchange();

                    final ServerWebExchange finalExchange = exchange;
                    return responseMono.flatMap(resp -> {
                        String receivedNewToken = resp.headers().asHttpHeaders().getFirst(HttpHeaders.AUTHORIZATION);

                        ServerHttpRequest originalReq = finalExchange.getRequest().mutate()
                                .uri(URI.create(originalUrl))
                                .header(HttpHeaders.AUTHORIZATION, receivedNewToken)
                                .build();

                        ServerWebExchange newExchange = finalExchange.mutate().request(originalReq).build();
                        return chain.filter(newExchange);
                    });
                }
            }
            // access token이 만료된 경우
        } catch (ExpiredJwtException e) {
            String uuid = e.getClaims().get("uuid").toString();
            // refresh token이 살아있다면
            if (tokenService.refreshTokenExists(uuid)) {

                WebClient webClient = WebClient.create();
                Mono<ClientResponse> responseMono = webClient.post()
                        .uri("http://localhost:8084/refresh")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + access)
                        .exchange();

                final ServerWebExchange finalExchange = exchange;
                return responseMono.flatMap(resp -> {
                    String receivedNewToken = resp.headers().asHttpHeaders().getFirst(HttpHeaders.AUTHORIZATION);

                    ServerHttpRequest originalReq = finalExchange.getRequest().mutate()
                            .uri(URI.create(originalUrl))
                            .header(HttpHeaders.AUTHORIZATION, receivedNewToken)
                            .build();

                    ServerWebExchange newExchange = finalExchange.mutate().request(originalReq).build();
                    return chain.filter(newExchange);
                });
            } else { // refresh token이 만료됬다면
                throw new TokenExpiredExceptions("Login again");
            }
        }
        // 나머지 재발급이 필요없는 경우 필터링
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
