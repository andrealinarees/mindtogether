package es.udc.asi.user.model.repository;

import java.util.Collection;

import es.udc.asi.user.model.domain.User;

public interface UserDao {

  Collection<User> findAll();

  User findById(Long id);

  User findByLogin(String login);

  void create(User user);

  void update(User user);

  void delete(User user);

}
