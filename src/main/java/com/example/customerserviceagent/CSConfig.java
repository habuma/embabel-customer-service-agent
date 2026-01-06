package com.example.customerserviceagent;

import com.embabel.agent.api.common.Actor;
import com.embabel.agent.prompt.persona.Persona;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("embabel.customerservice")
public class CSConfig {

  private Actor<Persona> customerService;

  public void setCustomerService(Actor<Persona> customerService) {
    this.customerService = customerService;
  }

  public Actor<Persona> getCustomerService() {
    return customerService;
  }

}
