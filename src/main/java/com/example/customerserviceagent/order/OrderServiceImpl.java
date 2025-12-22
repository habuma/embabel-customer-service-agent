package com.example.customerserviceagent.order;

import com.example.customerserviceagent.domain.OrderDetails;
import com.example.customerserviceagent.domain.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

  private OrderRepository orderRepository;

  public OrderServiceImpl(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  @Override
  public OrderDetails getOrderDetails(String orderNumber) {
    return orderRepository.findByOrderNumber(orderNumber);
  }
}
