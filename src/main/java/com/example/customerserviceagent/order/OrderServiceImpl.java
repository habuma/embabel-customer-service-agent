package com.example.customerserviceagent.order;

import com.example.customerserviceagent.domain.OrderDetails;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

  @Override
  public OrderDetails getOrderDetails(Long orderId) {
    return new OrderDetails(orderId, "ABC-1234", "SHIPPED", true, true);
  }
}
