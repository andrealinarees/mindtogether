package com.mindtogether.user.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mindtogether.user.model.exception.UserLoginExistsException;
import com.mindtogether.user.model.service.UserService;
import com.mindtogether.user.model.service.dto.LoginDTO;
import com.mindtogether.user.model.service.dto.UserDTOPrivate;
import com.mindtogether.user.security.JWTToken;
import com.mindtogether.user.security.TokenProvider;
import com.mindtogether.user.web.exceptions.CredentialsAreNotValidException;
import com.mindtogether.user.web.exceptions.RequestBodyNotValidException;
import jakarta.validation.Valid;

/**
 * Este controlador va por separado que el UserResource porque se encarga de
 * tareas relacionadas con la autenticación, registro, etc.
 *
 * <p>
 * También permite a cada usuario logueado en la aplicación obtener información
 * de su cuenta
 */
@RestController
@RequestMapping("/api/account")
public class AuthResource {
  private final Logger logger = LoggerFactory.getLogger(AuthResource.class);

  @Autowired
  private TokenProvider tokenProvider;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private UserService userService;

  @PostMapping("/authenticate")
  public JWTToken authenticate(@Valid @RequestBody LoginDTO loginDTO) throws CredentialsAreNotValidException {


    try {

      UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
        loginDTO.getLogin(), loginDTO.getPassword());
      Authentication authentication = authenticationManager.authenticate(authenticationToken);
      SecurityContextHolder.getContext().setAuthentication(authentication);
      String jwt = tokenProvider.createToken(authentication);
      return new JWTToken(jwt);
    } catch (AuthenticationException e) {
      logger.warn(e.getMessage(), e);
      throw new CredentialsAreNotValidException(e.getMessage());
    } catch (Exception exception) {
      logger.error("Exception", exception);
      throw new CredentialsAreNotValidException(exception.getMessage());
    }
  }

  @GetMapping("")
  public UserDTOPrivate getAccount() {
    return userService.getCurrentUserWithAuthority();
  }

  @PostMapping("/register")
  public void registerAccount(@Valid @RequestBody UserDTOPrivate account, Errors errors)
      throws UserLoginExistsException, RequestBodyNotValidException {
    if (errors.hasErrors()) {
      throw new RequestBodyNotValidException(errors);
    }

    userService.registerUser(account.getLogin(), account.getPassword(),
        account.getName(), account.getEmail(), account.getPhone(), account.getCity());
  }

  @PutMapping("")
  public UserDTOPrivate updateAccount(@RequestBody UserDTOPrivate account) {
    return userService.updateCurrentUser(account.getName(), account.getEmail(),
        account.getPhone(), account.getCity());
  }
}

