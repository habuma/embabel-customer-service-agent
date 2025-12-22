package com.example.customerserviceagent;

import com.example.customerserviceagent.domain.OrderDetails;
import com.example.customerserviceagent.domain.OrderRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class CustomerServiceAgentApplication {

  public static void main(String[] args) {
    SpringApplication.run(CustomerServiceAgentApplication.class, args);
  }

  @Bean
  ApplicationRunner go(OrderRepository orderRepository) {
    return args -> {
      orderRepository.saveAll(List.of(
          new OrderDetails(null, "112233", "ABC-123", "SHIPPED", false, true),
          new OrderDetails(null, "223344", "DEF-456", "UNSHIPPED", true, false),
          new OrderDetails(null, "334455", "GHI-789", "SHIPPED", false, false)));
    };
  }

}
