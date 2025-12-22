package com.example.customerserviceagent.domain;

import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<OrderDetails, Long> {

  OrderDetails findByOrderNumber(String orderNumber);

}
