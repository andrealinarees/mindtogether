package es.udc.asi.user.model.service.dto;

import es.udc.asi.user.model.domain.User;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class UserDTOPrivate {
  private Long id;

  @NotEmpty
  @Size(min = 4)
  private String login;

  @NotEmpty
  @Size(min = 4)
  private String password;
  private String authority;

  private String name;
  private String email;
  private String phone;
  private String city;

  public UserDTOPrivate() {
  }

  public UserDTOPrivate(User user) {
    this.id = user.getId();
    this.login = user.getLogin();
    // la contraseña no se rellena, nunca se envía al cliente
    this.authority = user.getAuthority().name();
    this.name = user.getName();
    this.email = user.getEmail();
    this.phone = user.getPhone();
    this.city = user.getCity();
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

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getAuthority() {
    return authority;
  }

  public void setAuthority(String authority) {
    this.authority = authority;
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

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }
}
