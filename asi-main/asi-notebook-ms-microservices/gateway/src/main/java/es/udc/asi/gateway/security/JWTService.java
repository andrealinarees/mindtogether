// Removed package declaration to match default package
package es.udc.asi.gateway.security;


import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.function.Function;
import java.util.logging.Logger;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import es.udc.asi.gateway.config.Properties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTService {
    
    private static final String AUTHORITIES_KEY = "auth";
    private static final String ID_KEY = "uid";

    @Autowired
    private Properties properties;   


    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {        
        SecretKey key = Keys.hmacShaKeyFor(properties.getJwtSecretKey().getBytes(StandardCharsets.UTF_8));
        return Jwts
                .parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token).getPayload();
    }


    public String getUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String getRoles(String token) {
        return extractAllClaims(token).get(AUTHORITIES_KEY).toString();
    }

    public Long getUid(String token) {
        String s= extractAllClaims(token).get(ID_KEY).toString();
        return s == null ? null : Long.parseLong(s);
    }   

    public Authentication getAuthentication(String token) {
        
        Claims claims = extractAllClaims(token);
        GrantedAuthority authority = new SimpleGrantedAuthority(claims.get(AUTHORITIES_KEY).toString());
        Collection<GrantedAuthority> authorities = Collections.singleton(authority);
        Long uid = Long.parseLong(claims.get(ID_KEY).toString());
        CustomUserDetails userDetails = new CustomUserDetails(claims.getSubject(), "", authorities, uid);
        return new UsernamePasswordAuthenticationToken(userDetails, token, authorities);
    }

    public boolean isTokenValid(String token) {
        try {
            
            Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token);            
            return !isTokenExpired(token);
        } catch (JwtException e) {
            return false;
        }
    }
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private SecretKey getSignInKey() {
       return Keys.hmacShaKeyFor(properties.getJwtSecretKey().getBytes(StandardCharsets.UTF_8));
    }

}