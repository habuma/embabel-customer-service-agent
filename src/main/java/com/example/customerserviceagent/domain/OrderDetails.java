package com.example.customerserviceagent.domain;

import com.embabel.common.ai.prompt.PromptContributionLocation;
import com.embabel.common.ai.prompt.PromptContributor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

public record OrderDetails(
    @Id
    @Column()
    Long id,
    String orderNumber,
    String sku,
    String shipmentStatus,
    boolean refundEligible,
    boolean resendEligible) implements PromptContributor {

  @Override
  public @NotNull String contribution() {
    return "Order Number: " + orderNumber +
         "\nSKU: " + sku +
         "\nShipment status: " + shipmentStatus +
         "\nRefund eligible: " + refundEligible +
         "\nResend eligible: " + resendEligible;
  }

  @Override
  public @NotNull PromptContributionLocation getPromptContributionLocation() {
    return PromptContributionLocation.BEGINNING;
  }

}
