package es.udc.asi.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Lazy;

import es.udc.asi.user.config.DatabaseLoader;
import es.udc.asi.user.model.exception.UserLoginExistsException;
import jakarta.annotation.PostConstruct;

@SpringBootApplication
@EnableFeignClients
public class UserApplication {
  private final Logger logger = LoggerFactory.getLogger(UserApplication.class);

  @Autowired
  @Lazy
  private DatabaseLoader databaseLoader;

  public static void main(String[] args) {
    SpringApplication.run(UserApplication.class, args);
  }

  @PostConstruct
  public void init() throws InterruptedException {
    try {
      databaseLoader.loadData();
    } catch (UserLoginExistsException e) {
      logger.error(e.getMessage(), e);
    }
  }
}
