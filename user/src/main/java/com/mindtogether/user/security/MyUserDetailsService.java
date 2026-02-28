package com.mindtogether.user.security;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.mindtogether.user.model.domain.User;
import com.mindtogether.user.model.repository.UserDao;

@Component
public class MyUserDetailsService implements UserDetailsService {
  private final Logger logger = LoggerFactory.getLogger(MyUserDetailsService.class);

  @Autowired
  private UserDao userDAO;

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
    logger.warn("Loading user " + login);
    try {
      User user = userDAO.findByLogin(login);
      if (user == null) {
        throw new UsernameNotFoundException("User " + login + " not found");
      }
      if (!user.isActive()) {
        throw new UsernameNotFoundException("User " + login + " not found");
      }
      logger.info("Loaded user {} with authority {}", login, user.getAuthority().name());
      GrantedAuthority authority = new SimpleGrantedAuthority(user.getAuthority().name());

      return new CustomUserDetails(login, user.getPassword(), Collections.singleton(authority), user.getId());
    } catch (Exception e) {
      logger.warn("Error loading user " + login, e);
      throw new UsernameNotFoundException("User " + login + " not found");
    }
  }
}

