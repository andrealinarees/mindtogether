package com.mindtogether.user.model.service.dto;

import com.mindtogether.user.model.domain.User;
import com.mindtogether.user.model.domain.UserAuthority;

public class UserDTOPublic {
  private Long id;
  private String login;
  private String name;
  private String email;
  private boolean active = true;
  private UserAuthority authority;

  public UserDTOPublic() {
  }

  public UserDTOPublic(User user) {
    this.id = user.getId();
    this.login = user.getLogin();
    this.name = user.getName();
    this.email = user.getEmail();
    this.active = user.isActive();
    this.authority = user.getAuthority();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public UserAuthority getAuthority() {
    return authority;
  }

  public void setAuthority(UserAuthority authority) {
    this.authority = authority;
  }
}

