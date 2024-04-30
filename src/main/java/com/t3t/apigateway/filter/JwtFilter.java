package com.t3t.apigateway.filter;

import com.t3t.apigateway.common.JwtUtils;
import com.t3t.apigateway.exception.TokenNotAuthenticatedExceptions;
import com.t3t.apigateway.exception.TokenNotExistExceptions;
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
import java.util.Arrays;
import java.util.Objects;

/**
 * 인증이 필요한 url로 요청이 들어온 경우, 인가처리를 진행하는 필터
 * @author joohyun1996(이주현)
 */
@Component
@RequiredArgsConstructor
public class JwtFilter implements GatewayFilter {
    private final JwtUtils jwtUtils;

    /**
     * 토큰의 signature, expiration등을 확인하는 필터
     * 요청이 합당하면 경로 재지정을 수행한다
     * @param exchange,chain
     * @author joohyun1996(이주현)
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String url = exchange.getRequest().getURI().getPath();

        // 인가 처리가 필요없는 경우는 필터링
        if (!url.startsWith("/at")) {
            return chain.filter(exchange);
        }

        // access 토큰이 필요한데 없는 경우는 에러 발생
        // 글로벌 필터에서 이미 다 처리했지만, 한번 더 처리 진행
        if (Objects.isNull(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))){
            throw new TokenNotExistExceptions("Access Token Not Exists");
        }

        String access = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION).trim().split(" ")[1];

        // token이 서버가 발행한게 맞는지, 혹은 만료되지 않았는지 확인
        if (jwtUtils.getValidation(access)) {
            throw new TokenNotAuthenticatedExceptions("Not Authenticated Token");
        }
        
        // 경로 재작성 Logic
        // "/at/serviceName/*/*" -> "/*/*"
        if (url.startsWith("/at")){
            String[] strs = url.split("/");
            String newPath = "/" + String.join("/", Arrays.copyOfRange(strs, 3, strs.length));
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
