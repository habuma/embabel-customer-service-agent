package com.example.customerserviceagent;

import com.embabel.agent.api.annotation.*;
import com.embabel.agent.api.common.Ai;
import com.example.customerserviceagent.domain.*;
import com.example.customerserviceagent.order.OrderService;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.util.Map;

@Agent(name = "customerServiceAgent",
      description = "Addresses customer order issues")
public class CustomerServiceAgent {

  @Value("classpath:/promptTemplates/issueClassification.st")
  private Resource issueClassificationPT;

  @Value("classpath:/promptTemplates/determineResolutionPlan.st")
  private Resource determineResolutionPlanPT;

  @Value("classpath:/promptTemplates/finalResponse.st")
  private Resource finalResponsePT;

  private final OrderService orderService;

  public CustomerServiceAgent(OrderService orderService) {
    this.orderService = orderService;
  }

  @Action(description = "Classifies the issue based on customer input")
  public IssueClassification classifyIssue(CustomerInput customerInput, Ai ai) {
    var prompt = PromptTemplate.builder().resource(issueClassificationPT)
        .variables(Map.of("input", customerInput.text()))
        .build()
        .render();

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

    var prompt = PromptTemplate.builder().resource(determineResolutionPlanPT)
        .variables(Map.of(
            "issueType", issueClassification.issueType().name(),
            "shipmentStatus", orderDetails.shipmentStatus(),
            "refundEligible", orderDetails.refundEligible(),
            "resendEligible", orderDetails.resendElibible()))
        .build()
        .render();

    return ai.withDefaultLlm()
        .createObject(prompt, ResolutionPlan.class);
  }

  @Action(description = "Execute resolution")
  public ResolutionConfirmation executeResolution(OrderDetails orderDetails, ResolutionPlan resolutionPlan) {
    if (resolutionPlan.resolutionType().equals(ResolutionType.REFUND)) {
      return new ResolutionConfirmation("REFUND-1234");
    } else if (resolutionPlan.resolutionType().equals(ResolutionType.RESEND_ITEM)) {
      return new ResolutionConfirmation("RESEND-1234");
    }
    return new ResolutionConfirmation("CONTACT-1234");
  }

  @Action(description = "Give final response")
  @AchievesGoal(description = "Issue is resolved",
      export = @Export(name = "finalResponse", remote = true, startingInputTypes = CustomerInput.class))
  public FinalResponse resolveIssue(OrderDetails orderDetails,
                                    ResolutionPlan resolutionPlan,
                                    ResolutionConfirmation resolutionConfirmation,
                                    Ai ai) {

    var prompt = PromptTemplate.builder().resource(finalResponsePT)
        .variables(Map.of(
            "orderId", orderDetails.orderId(),
            "resolutionType", resolutionPlan.resolutionType().name(),
            "resolutionConfirmationId", resolutionConfirmation.id()))
        .build()
        .render();

    return ai.withDefaultLlm()
        .createObject(prompt, FinalResponse.class);
  }

}
