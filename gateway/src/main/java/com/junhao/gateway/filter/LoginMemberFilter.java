package com.junhao.gateway.filter;

import com.junhao.gateway.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
@Component
public class LoginMemberFilter implements Ordered, GlobalFilter {
    private static final Logger LOG = LoggerFactory.getLogger(LoginMemberFilter.class);
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // 排除不需要拦截的请求
        if (path.contains("/admin")
                || path.contains("/hello")
                || path.contains("/member/member/login")
                || path.contains("/member/member/send-code")) {
            LOG.info("不需要登录验证：{}", path);
            return chain.filter(exchange);
        } else {
            LOG.info("需要登录验证：{}", path);
        }
        // 获取header的token参数（兼容 token / Token 两种头名）
        String token = exchange.getRequest().getHeaders().getFirst("token");
        if (token == null) {
            token = exchange.getRequest().getHeaders().getFirst("Token");
        }
        if (token != null) {
            token = token.trim();
            // 兼容前端使用 "Bearer xxx" 标准写法
            if (token.toLowerCase().startsWith("bearer ")) {
                token = token.substring(7).trim();
            }
            // 兼容 http 客户端把引号也放进 header 值的情况
            if (token.startsWith("\"") && token.endsWith("\"")) {
                token = token.substring(1, token.length() - 1);
            }
        }
        LOG.info("会员登录验证开始，token：{}", token);
        if (token == null || token.isEmpty()) {
            LOG.info( "token为空，请求被拦截" );
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 校验token是否有效，包括token是否被改过，是否过期
        boolean validate = JwtUtil.validate(token);
        if (validate) {
            LOG.info("token有效，放行该请求");
            return chain.filter(exchange);
        } else {
            LOG.warn( "token无效，请求被拦截" );
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
    /**
     * 过滤器执行顺序值越小  优先级越高
     * @return 0
     */

    @Override
    public int getOrder() {
        return 0;
    }
}
