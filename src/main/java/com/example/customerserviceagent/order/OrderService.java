package com.example.customerserviceagent.order;

import com.example.customerserviceagent.domain.OrderDetails;

public interface OrderService {

  OrderDetails getOrderDetails(String orderNumber);

}
