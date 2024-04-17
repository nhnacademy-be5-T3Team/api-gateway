package com.t3t.apigateway.filter;

import com.t3t.apigateway.common.JwtUtils;
import com.t3t.apigateway.exception.TokenNotExistExceptions;
import com.t3t.apigateway.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Objects;

@RequiredArgsConstructor
@Component
public class GlobalHttpReIssueFilter implements GlobalFilter, Ordered {
    private final JwtUtils jwtUtils;
    private final TokenService tokenService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String originalUrl = exchange.getRequest().getURI().getPath();
        // 요청에 토큰이 없는 경우는 pass
        if(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION).isEmpty()){
            return chain.filter(exchange);
        }
        String access = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION).trim().split(" ")[1];
        if(Objects.isNull(access)){
            throw new TokenNotExistExceptions("No Such Token!");
        }
        // 토큰 만료 시간이 5분 넘게 남은 경우는 pass
        if(!jwtUtils.checkReIssue(access)){
            return chain.filter(exchange);
        }
        // 토큰 만료 시간이 5분보다 적은 경우
        // Refresh 토큰이 만료된 경우
        if(!tokenService.refreshTokenExists(jwtUtils.getUUID(access))){
            return chain.filter(exchange);
        }else{ // Refresh 토큰이 있는 경우 해당 url로 전송
            ServerHttpRequest request = exchange.getRequest().mutate().uri(URI.create("/at/auth/refresh")).build();
            exchange = exchange.mutate().request(request).build();

            WebClient webClient = WebClient.create();
            Mono<String> responseMono = webClient.post()
                    .uri("/at/auth/refresh")
                    .retrieve()
                    .bodyToMono(String.class);

            final ServerWebExchange finalExchange = exchange;
            return responseMono.flatMap(resp -> {
               ServerHttpRequest originalReq = finalExchange.getRequest().mutate().uri(URI.create(originalUrl)).build();
               ServerWebExchange newExchange = finalExchange.mutate().request(originalReq).build();
               return chain.filter(newExchange);
            });
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
