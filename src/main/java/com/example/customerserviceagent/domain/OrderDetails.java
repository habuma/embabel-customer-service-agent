package com.example.customerserviceagent.domain;

public record OrderDetails(
    Long orderId,
    String sku,
    String shipmentStatus,
    boolean refundEligible,
    boolean resendElibible) {}
