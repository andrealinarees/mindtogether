package es.udc.asi.gateway.security;

import java.util.logging.Logger;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Configuration
public class JwtServerAuthenticationConverter implements ServerAuthenticationConverter {

    public static final String AUTHORIZATION_HEADER = "Authorization";


    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {        
        Mono<Authentication> res = Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(AUTHORIZATION_HEADER))
                .filter(authHeader -> authHeader.startsWith("Bearer "))
                .map(authHeader -> authHeader.substring(7))
                .map(token -> new UsernamePasswordAuthenticationToken(null, token));
        return res;
    }

}