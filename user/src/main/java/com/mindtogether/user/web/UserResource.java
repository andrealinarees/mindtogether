package com.mindtogether.user.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mindtogether.user.model.exception.NotFoundException;
import com.mindtogether.user.model.exception.OperationNotAllowed;
import com.mindtogether.user.model.service.UserService;
import com.mindtogether.user.model.service.dto.UserDTOPublic;
import com.mindtogether.user.model.service.dto.UserWithNotesDTO;

@RestController
@RequestMapping("/api/users")
public class UserResource {

  @Autowired
  private UserService userService;

  @GetMapping
  public List<UserDTOPublic> findAll() {
    return userService.findAll();
  }

  @GetMapping("/stats")
  public Map<String, Object> getStats() {
    Map<String, Object> stats = new HashMap<>();
    List<UserDTOPublic> allUsers = userService.findAll();

    long totalUsers = allUsers.size();
    long activeUsers = allUsers.stream().filter(UserDTOPublic::isActive).count();
    long inactiveUsers = totalUsers - activeUsers;

    stats.put("totalUsers", totalUsers);
    stats.put("activeUsers", activeUsers);
    stats.put("inactiveUsers", inactiveUsers);

    return stats;
  }

  @GetMapping("/{id}")
  public UserWithNotesDTO findOne(@PathVariable Long id) throws NotFoundException {
    return userService.findOne(id);
  }

  @PutMapping("/{id}/active")
  public UserDTOPublic activate(@PathVariable Long id) throws NotFoundException, OperationNotAllowed {
    return userService.updateActive(id, true);
  }

  @DeleteMapping("/{id}/active")
  public UserDTOPublic deactivate(@PathVariable Long id) throws NotFoundException, OperationNotAllowed {
    return userService.updateActive(id, false);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) throws NotFoundException, OperationNotAllowed {
    userService.deleteById(id);
  }
}

