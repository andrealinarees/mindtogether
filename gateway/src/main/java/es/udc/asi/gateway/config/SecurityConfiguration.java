package es.udc.asi.gateway.config;

import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import es.udc.asi.gateway.security.CustomUserDetails;
import es.udc.asi.gateway.security.JwtServerAuthenticationConverter;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

    
    private final Logger logger = Logger.getLogger(SecurityConfiguration.class.getName());

    @Autowired
    private Properties properties;

    public SecurityConfiguration() {
    
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         WebFilter headerCleaningFilter,
                                                         AuthenticationWebFilter jwtAuthenticationWebFilter) {
        return http                
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .headers((headers) -> headers.frameOptions((frameOptions) -> frameOptions.disable()))
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())                
                .authorizeExchange(exchanges -> exchanges                    
                    .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .pathMatchers(HttpMethod.GET, "/actuator/**").permitAll()
                    .pathMatchers(HttpMethod.POST, "/user/authenticate").permitAll()
                    .pathMatchers(HttpMethod.POST, "/api/account/authenticate").permitAll()
                    .pathMatchers(HttpMethod.POST, "/api/account/register").permitAll()
                   // .pathMatchers(properties.getPublicPaths().toArray(new String[0])).permitAll()
                    .pathMatchers("/**").authenticated())
                .addFilterAt(headerCleaningFilter, SecurityWebFiltersOrder.FIRST)
                .addFilterAt(jwtAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)                        
                .build();
    }


    @Bean
    public WebFilter headerCleaningFilter() {
        return (exchange, chain) -> {
            logger.info(exchange.getRequest().getMethod().name()+":"+exchange.getRequest().getPath().toString());            
            ServerWebExchange mutatedExchange = exchange.mutate()
                .request(builder -> builder.headers(httpHeaders -> {
                    httpHeaders.remove("X-User");
                    httpHeaders.remove("X-User-Id");
                    httpHeaders.remove("X-Roles");
                }))
                .build();
            return chain.filter(mutatedExchange);
        };
    }


    @Bean
    public AuthenticationWebFilter jwtAuthenticationWebFilter(JwtReactiveAuthenticationManager authManager,
                                                              JwtServerAuthenticationConverter converter) {
        AuthenticationWebFilter filter = new AuthenticationWebFilter(authManager) {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
                logger.info("AuthFilter");
                return super.filter(exchange, chain);
            }

        };
        filter.setServerAuthenticationConverter(converter);
        filter.setAuthenticationSuccessHandler(new ServerAuthenticationSuccessHandler() {
            @Override
            public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
                ServerWebExchange exchange = webFilterExchange.getExchange();

                String username = authentication.getName();       
                Long uid = ((CustomUserDetails)authentication.getPrincipal()).getUid();         
                String roles = authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(","));                
                
                ServerWebExchange mutatedExchange = exchange.mutate()
                        .request(builder -> builder
                                .header("X-User", username)
                                .header("X-User-Id", uid.toString())
                                .header("X-Roles", roles))
                        .build();
                
                return webFilterExchange.getChain().filter(mutatedExchange);
            }
        });
       
        return filter;
    }

    
}