package com.t3t.apigateway.filter;

import com.t3t.apigateway.exception.BlackListTokenExceptions;
import com.t3t.apigateway.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * HttpServletRequest로 들어온 요청의 JWT가 Logout된 토큰인지 확인하는 필터
 * @author joohyun1996(이주현)
 */
@RequiredArgsConstructor
@Component
public class GlobalFindBlackListFilter implements GlobalFilter, Ordered {
    private final TokenService tokenService;

    /**
     * request를 검사해 access token이 있는지, blacklist에 등록된 토큰인지 확인하는 필터
     * @param exchange,chain
     * @author joohyun1996(이주현)
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        // Token이 없는 요청은 검사하지 않는다.
        if(Objects.isNull(request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION))){
            return chain.filter(exchange);
        }
        String access = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION).trim().substring(7);

        // 만약 BlackList 로 등록된 토큰이라면 throw
        if(tokenService.findBlackList(access)){
           throw new BlackListTokenExceptions("Don't Use Expired Token");
        }

        // 아니면 다음 필터로 이동
        return chain.filter(exchange);
    }

    /**
     * 최우선 순위로 필터링
     * @author joohyun1996(이주현)
     */
    @Override
    public int getOrder() {
        return -2;
    }
}
