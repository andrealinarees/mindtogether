package es.udc.asi.gateway.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "gateway.config")
public class Properties {

  private String clientHost;
  private String jwtSecretKey;
  private Long jwtValidity;

  private List<String> publicPaths;
  private List<String> authenticatedPaths;

  public String getClientHost() {
    return clientHost;
  }

  public void setClientHost(String clientHost) {
    this.clientHost = clientHost;
  }

  public String getJwtSecretKey() {
    return jwtSecretKey;
  }

  public void setJwtSecretKey(String jwtSecretKey) {
    this.jwtSecretKey = jwtSecretKey;
  }

  public Long getJwtValidity() {
    return jwtValidity;
  }

  public void setJwtValidity(Long jwtValidity) {
    this.jwtValidity = jwtValidity;
  }

  public List<String> getPublicPaths() {
    return publicPaths;
  }

  public void setPublicPaths(List<String> publicPaths) {
    this.publicPaths = publicPaths;
  }


  public List<String> getAuthenticatedPaths() {
    return authenticatedPaths;
  }

  public void setAuthenticatedPaths(List<String> authenticatedPaths) {
    this.authenticatedPaths = authenticatedPaths;
  }

  
}
