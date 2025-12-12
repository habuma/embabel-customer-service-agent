package com.example.customerserviceagent;

import com.embabel.agent.api.annotation.*;
import com.embabel.agent.api.common.Ai;
import com.example.customerserviceagent.domain.*;
import com.example.customerserviceagent.order.OrderService;

@Agent(name = "customerServiceAgent",
      description = "Addresses customer order issues")
public class CustomerServiceAgent {

  private final OrderService orderService;

  public CustomerServiceAgent(OrderService orderService) {
    this.orderService = orderService;
  }

  @Action(description = "Classifies the issue based on customer input")
  public IssueClassification classifyIssue(CustomerInput customerInput, Ai ai) {
    var promptTemplate = """
        Based on the customer's input, classify the issue as one of the following:
        MISSING_ITEM, DAMAGED_ITEM, WRONG_ITEM, DELAYED, REFUND_REQUEST, or OTHER.
        
        Customer input: {input}
        """;
    var prompt = promptTemplate.replace("{input}", customerInput.text());

    return ai.withDefaultLlm()
        .createObject(prompt, IssueClassification.class);
  }

  @Action(description = "Gets the order details")
  public OrderDetails checkOrderStatus(CustomerInput customerInput) {
    Long orderId = customerInput.orderId();
    if (orderId == null) {
      OrderIdInput orderIdInput = WaitFor.formSubmission("What is the order ID?", OrderIdInput.class);
      orderId = orderIdInput.orderId();
    }

    return orderService.getOrderDetails(orderId);
  }

  @Action(description = "Determine resolution plan")
  public ResolutionPlan determineResolutionPlan(
      IssueClassification issueClassification,
      OrderDetails orderDetails,
      Ai ai) {

    var promptTemplate = """
        Given the issue classification and order details, determine a resolution plan from
        one of the following: REFUND, RESEND_ITEM, CONTACT_CUSTOMER
        
        Issue Classification: {issueType}
        
        Order Details:
        - Shipment Status: {shipmentStatus}
        - Refund Eligible: {refundEligible}
        - Resend Eligible: {resendEligible}
        """;

    var prompt = promptTemplate
        .replace("{issueType}", issueClassification.issueType().name())
        .replace("{shipmentStatus", orderDetails.shipmentStatus())
        .replace("{refundEligible}", Boolean.toString(orderDetails.refundEligible()))
        .replace("{resendEligible}", Boolean.toString(orderDetails.resendElibible()));

    return ai.withDefaultLlm()
        .createObject(prompt, ResolutionPlan.class);
  }

  @Action(description = "Execute resolution")
  public ResolutionConfirmation executeResolution(OrderDetails orderDetails, ResolutionPlan resolutionPlan) {
    if (resolutionPlan.resolutionType().equals(ResolutionType.REFUND)) {
      System.err.println(" *** ISSUING REFUND FOR ORDER : " + orderDetails.orderId() + " ***");
      return new ResolutionConfirmation("REFUND-1234");
    } else if (resolutionPlan.resolutionType().equals(ResolutionType.RESEND_ITEM)) {
      System.err.println(" *** ISSUING RESEND FOR ORDER : " + orderDetails.orderId() + " ***");
      return new ResolutionConfirmation("RESEND-1234");
    }
    System.err.println(" *** CONTACT CUSTOMER FOR ORDER : " + orderDetails.orderId() + " ***");
    return new ResolutionConfirmation("CONTACT-1234");
  }

  @Action(description = "Give final response")
  @AchievesGoal(description = "Issue is resolved",
      export = @Export(name = "finalResponse", remote = true, startingInputTypes = CustomerInput.class))
  public FinalResponse resolveIssue(OrderDetails orderDetails,
                                    ResolutionPlan resolutionPlan,
                                    ResolutionConfirmation resolutionConfirmation,
                                    Ai ai) {

    var promptTemplate = """
        Generate a final response to the customer including the resolution details.
        
        Order Id: {orderId}
        Resolution: {resolutionType}
        Confirmation ID: {resolutionConfirmationId}
        """;

    var prompt = promptTemplate
        .replace("{orderId}", Long.toString(orderDetails.orderId()))
        .replace("{resolutionType}", resolutionPlan.resolutionType().name())
        .replace("{resolutionConfirmationId}", resolutionConfirmation.id());

    return ai.withDefaultLlm()
        .createObject(prompt, FinalResponse.class);
  }

}
