package es.udc.asi.gateway.config;

import java.util.logging.Logger;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;

import es.udc.asi.gateway.security.JWTService;
import reactor.core.publisher.Mono;


@Configuration
public class JwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final JWTService jwtService;
    private Logger logger = Logger.getLogger(JwtReactiveAuthenticationManager.class.getName());

    public JwtReactiveAuthenticationManager(JWTService jwtService) {
        logger.info("Instantiating JwtReactiveAuthenticationManager");
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) { 
        logger.info("Authenticate called");       
        String token = authentication.getCredentials().toString();
        if (!jwtService.isTokenValid(token)) {
            return Mono.error(new BadCredentialsException("Invalid JWT token"));
        }
        
        return Mono.just(jwtService.getAuthentication(token));
    }
}