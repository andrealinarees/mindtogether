package es.udc.asi.gateway.security;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import reactor.core.publisher.Mono;

import es.udc.asi.gateway.config.Properties;
import es.udc.asi.gateway.security.JWTService;

@Component
public class JwtGlobalFilter implements GlobalFilter, Ordered {



    @Autowired
    private JWTService jwtService;

    @Autowired
    private Properties properties;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().pathWithinApplication().value();

        // Always allow CORS preflight
        if (HttpMethod.OPTIONS.matches(exchange.getRequest().getMethod().name())) {
            return chain.filter(exchange);
        }

        // Allow explicit public endpoints defined in properties
        List<String> publicPaths = properties.getPublicPaths();
        if (publicPaths != null) {
            for (String publicPath : publicPaths) {
                if (publicPath != null && match(publicPath, path)) {
                    return chain.filter(exchange);
                }
            }
        }

        // Allow authentication endpoint (login)
        if (HttpMethod.POST.matches(exchange.getRequest().getMethod().name()) && "/api/account/authenticate".equals(path)) {
            return chain.filter(exchange);
        }

        // Check Authorization header
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        if (!jwtService.isTokenValid(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String username = jwtService.getUsername(token);
        String roles = jwtService.getRoles(token);
        Long userId = jwtService.getUid(token);

        ServerWebExchange mutatedExchange = exchange.mutate()
            .request(builder -> builder
                .header("X-User", username)
                .header("X-Roles", roles)
                .header("X-User-Id", userId == null ? "" : userId.toString()))
            .build();

        return chain.filter(mutatedExchange);
    }

    private boolean match(String pattern, String path) {
        // Allow patterns like /api/account/** or exact matches
        if (pattern.contains("*") || pattern.contains("?")) {
            return pathMatcher.match(pattern, path);
        }
        return Objects.equals(pattern, path) || path.startsWith(pattern);
    }

    @Override
    public int getOrder() {
        // Run before routing to downstream services; keep high precedence
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }
}
