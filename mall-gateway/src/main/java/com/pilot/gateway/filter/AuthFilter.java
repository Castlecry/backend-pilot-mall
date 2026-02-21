package com.pilot.gateway.filter;

import com.pilot.common.utils.JwtUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthFilter implements GlobalFilter, Ordered {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String USER_ID_HEADER = "X-User-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        //白名单：
        if (path.contains("/user/login") || path.contains("/ai/chat")) {
            return chain.filter(exchange);
        }
        //拿 Token
        String authHeader = request.getHeaders().getFirst(AUTHORIZATION_HEADER);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        //检验Token
        String token = authHeader.substring(BEARER_PREFIX.length());

        if (!JwtUtils.validateToken(token)) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        // 从 Token 里解析出 UserId
        Long userId = JwtUtils.getUserIdFromToken(token);
        // 在请求头里强行塞入一个 "X-User-Id"
        ServerHttpRequest modifiedRequest = request.mutate()
                .header(USER_ID_HEADER, String.valueOf(userId))
                .build();
        // 将变异后的请求传递给下一个过滤器（也就是转发给下游微服务）
        ServerWebExchange modifiedExchange = exchange.mutate()
                .request(modifiedRequest)
                .build();

        return chain.filter(modifiedExchange);
    }
    // 返回值越小，优先级越高。
    @Override
    public int getOrder() {
        return -100;
    }
}
