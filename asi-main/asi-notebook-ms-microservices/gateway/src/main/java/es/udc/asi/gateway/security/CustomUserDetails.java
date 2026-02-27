package es.udc.asi.gateway.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Custom UserDetails that carries the user's id (uid) in addition to username and authorities.
 */
public class CustomUserDetails implements UserDetails {

  private final String username;
  private final String password;
  private final Collection<? extends GrantedAuthority> authorities;
  private final Long uid;

  public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, Long uid) {
    this.username = username;
    this.password = password == null ? "" : password;
    this.authorities = authorities;
    this.uid = uid;
  }

  public Long getUid() {
    return uid;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

}
