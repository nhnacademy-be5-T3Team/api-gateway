package com.t3t.apigateway.config;

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
     */
    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("bookstore-service", r -> r.path("/bookstore/**")
                        .filters(f -> f.rewritePath("/bookstore/(?<segment>.*)", "/${segment}"))
                        .uri("lb://BOOKSTORE-SERVICE"))
                .route("coupon-service", r -> r.path("/coupon/**")
                        .filters(f -> f.rewritePath("/coupon/(?<segment>.*)", "/${segment}"))
                        .uri("lb://COUPON-SERVICE"))
                .route("auth-service", r -> r.path("/auth/**")
                        .filters(f -> f.rewritePath("/auth/(?<segment>.*)", "/${segment}"))
                        .uri("lb://AUTH-SERVICE"))
                .build();
    }
}