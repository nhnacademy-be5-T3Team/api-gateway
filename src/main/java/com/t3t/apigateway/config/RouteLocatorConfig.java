package com.t3t.apigateway.config;

import com.t3t.apigateway.common.JwtUtils;
import com.t3t.apigateway.filter.JwtFilter;
import com.t3t.apigateway.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteLocatorConfig {
    /**
     * 각 서비스로 라우팅 및 로드밸런싱 설정을 위한 빈<br>
     * @apiNote rewrite filter 를 통해 서비스 구분에 사용되는 경로를 제거하여 서비스로 라우팅한다.
     * @author woody35545(구건모)
     *
     */

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder, JwtFilter jwtFilter) {
        /*secrets -> open*/
        return builder.routes()
                // 추후 수정 필요
                // bookstore/members/** 이런식으로 하나하나 찾아서 filter 걸기
                .route("bookstore-service-secrets", r-> r.path("/at/bookstore/**")
                        .filters(f->f.filter(jwtFilter))
                        .uri("lb://BOOKSTORE-SERVICE"))
                .route("bookstore-service", r -> r.path("/t3t/bookstore/**")
                        .filters(f -> f.rewritePath("/t3t/bookstore/(?<segment>.*)", "/${segment}"))
                        .uri("lb://BOOKSTORE-SERVICE"))
                // 추후 수정 필요
                .route("auth-service-secrets", r -> r.path("/at/auth/**")
                        .filters(f->f.filter(jwtFilter))
                        .uri("lb://AUTH-SERVICE"))
                .route("auth-service", r -> r.path("/t3t/auth/**")
                        .filters(f -> f.rewritePath("/t3t/auth/(?<segment>.*)", "/${segment}"))
                        .uri("lb://AUTH-SERVICE"))
                .route("coupon-service", r -> r.path("/coupon/**")
                        .filters(f -> f.rewritePath("/coupon/(?<segment>.*)", "/${segment}"))
                        .uri("lb://COUPON-SERVICE"))
                .build();
    }
}