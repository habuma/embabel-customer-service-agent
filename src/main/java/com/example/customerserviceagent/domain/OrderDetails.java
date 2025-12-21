package com.example.customerserviceagent.domain;

import com.embabel.common.ai.prompt.PromptContributionLocation;
import com.embabel.common.ai.prompt.PromptContributor;
import org.jetbrains.annotations.NotNull;

public record OrderDetails(
    Long orderId,
    String sku,
    String shipmentStatus,
    boolean refundEligible,
    boolean resendElibible) implements PromptContributor {

  @Override
  public @NotNull String contribution() {
    return "Order ID: " + orderId +
         "\nSKU: " + sku +
         "\nShipment status: " + shipmentStatus +
         "\nRefund eligible: " + refundEligible +
         "\nResend eligible: " + resendElibible;
  }

  @Override
  public @NotNull PromptContributionLocation getPromptContributionLocation() {
    return PromptContributionLocation.BEGINNING;
  }

}
