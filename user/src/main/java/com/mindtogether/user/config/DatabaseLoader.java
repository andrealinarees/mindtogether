package com.mindtogether.user.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import com.mindtogether.user.model.domain.User;
import com.mindtogether.user.model.exception.UserLoginExistsException;
import com.mindtogether.user.model.repository.UserDao;
import com.mindtogether.user.model.service.UserService;

@Configuration
public class DatabaseLoader {

  @Autowired
  private UserDao userDAO;

  @Autowired
  private UserService userService;



  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public void loadData() throws UserLoginExistsException, InterruptedException {
    userService.registerUser("pepemin", "pepemin", "Pepe Administrador", "pepe@admin.com", "+34 600 000 001", "A Coruña", true);
    userService.registerUser("mariadmin", "mariadmin", "María Administradora", "maria@admin.com", "+34 600 000 002", "Santiago", true);
    userService.registerUser("laura", "laura", "Laura García", "laura@email.com", "+34 600 000 003", "Vigo");
    userService.registerUser("pedroff", "pedroff", "Pedro Fernández", "pedro@email.com", "+34 600 000 004", "Lugo");
    User pedro = userDAO.findByLogin("pedroff");
    pedro.setActive(false);
    userDAO.update(pedro);
    userService.registerUser("ramón", "ramón", "Ramón López", "ramon@email.com", "+34 600 000 005", "Ourense");

  }

}

