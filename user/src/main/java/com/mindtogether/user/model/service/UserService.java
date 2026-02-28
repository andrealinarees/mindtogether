package com.mindtogether.user.model.service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mindtogether.user.model.service.dto.NoteDTO;
import com.mindtogether.user.client.NoteServiceClient;
import com.mindtogether.user.model.domain.User;
import com.mindtogether.user.model.domain.UserAuthority;
import com.mindtogether.user.model.exception.NotFoundException;
import com.mindtogether.user.model.exception.OperationNotAllowed;
import com.mindtogether.user.model.exception.UserLoginExistsException;
import com.mindtogether.user.model.repository.UserDao;
import com.mindtogether.user.model.service.dto.UserDTOPrivate;
import com.mindtogether.user.model.service.dto.UserDTOPublic;
import com.mindtogether.user.model.service.dto.UserWithNotesDTO;
import com.mindtogether.user.security.SecurityUtils;

@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class UserService {

  @Autowired
  private UserDao userDAO;

  @Autowired private NoteServiceClient noteServiceClient;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @PreAuthorize("hasAuthority('ADMIN')")
  public List<UserDTOPublic> findAll() {
    Stream<UserDTOPublic> users = userDAO.findAll().stream().map(user -> new UserDTOPublic(user));
    if (SecurityUtils.getCurrentUserIsAdmin()) {
      return users.collect(Collectors.toList());
    }
    return users.filter(user -> user.isActive()).collect(Collectors.toList());
  }

  // permit any authenticated user to lookup basic info about another user (needed by frontend)
  @PreAuthorize("isAuthenticated()")
  public UserWithNotesDTO findOne(Long id) throws NotFoundException {
    User user = userDAO.findById(id);
    if (user == null) {
      throw new NotFoundException(id.toString(), User.class);
    }
    Collection<NoteDTO> notes = noteServiceClient.findForUser(user.getLogin());
    return new UserWithNotesDTO(user, notes);
  }

  @Transactional(readOnly = false)
  public void registerUser(String login, String password) throws UserLoginExistsException {
    registerUser(login, password, null, null, null, null, false);
  }

  @Transactional(readOnly = false)
  public void registerUser(String login, String password, String name, String email, String phone, String city) throws UserLoginExistsException {
    registerUser(login, password, name, email, phone, city, false);
  }

  @Transactional(readOnly = false)
  public void registerUser(String login, String password, String name, String email, String phone, String city, boolean isAdmin) throws UserLoginExistsException {
    if (userDAO.findByLogin(login) != null) {
      throw new UserLoginExistsException(login);
    }

    User user = new User();
    String encryptedPassword = passwordEncoder.encode(password);

    user.setLogin(login);
    user.setPassword(encryptedPassword);
    user.setName(name);
    user.setEmail(email);
    user.setPhone(phone);
    user.setCity(city);
    user.setAuthority(UserAuthority.USER);
    if (isAdmin) {
      user.setAuthority(UserAuthority.ADMIN);
    }

    userDAO.create(user);
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @Transactional(readOnly = false)
  public UserDTOPublic updateActive(Long id, boolean active) throws NotFoundException, OperationNotAllowed {
    User user = userDAO.findById(id);
    if (user == null) {
      throw new NotFoundException(id.toString(), User.class);
    }

    UserDTOPrivate currentUser = getCurrentUserWithAuthority();
    if (currentUser.getId().equals(user.getId())) {
      throw new OperationNotAllowed("The user cannot activate/deactive itself");
    }

    user.setActive(active);
    userDAO.update(user);
    return new UserDTOPublic(user);
  }

  public UserDTOPrivate getCurrentUserWithAuthority() {
    String currentUserLogin = SecurityUtils.getCurrentUserLogin();
    if (currentUserLogin != null) {
      return new UserDTOPrivate(userDAO.findByLogin(currentUserLogin));
    }
    return null;
  }

  public UserDTOPublic findByLogin(String login) {
    return new UserDTOPublic(userDAO.findByLogin(login));
  }

  @Transactional(readOnly = false)
  public UserDTOPrivate updateCurrentUser(String name, String email, String phone, String city) {
    String currentUserLogin = SecurityUtils.getCurrentUserLogin();
    if (currentUserLogin != null) {
      User user = userDAO.findByLogin(currentUserLogin);
      if (user != null) {
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setCity(city);
        userDAO.update(user);
        return new UserDTOPrivate(user);
      }
    }
    return null;
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @Transactional(readOnly = false)
  public void deleteById(Long id) throws NotFoundException, OperationNotAllowed {
    User theUser = userDAO.findById(id);
    if (theUser == null) {
      throw new NotFoundException(id.toString(), User.class);
    }
    UserDTOPrivate currentUser = getCurrentUserWithAuthority();
    if (currentUser.getId().equals(theUser.getId())) {
      throw new OperationNotAllowed("The user cannot remove itself");
    }

    //Problema incompleto: al borrar un usuario no necesariamente se están borrando sus notas.
    // Hay que comunicar el borrado a NoteService para que se eliminen las notas allí

    userDAO.delete(theUser);
  }

}

