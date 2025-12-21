package com.example.customerserviceagent;

import com.embabel.agent.api.annotation.*;
import com.embabel.agent.api.common.Ai;
import com.example.customerserviceagent.domain.*;
import com.example.customerserviceagent.order.OrderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

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
  public IssueClassification classifyIssue(CustomerInput customerInput, Ai ai) throws IOException {
    var prompt = issueClassificationPT.getContentAsString(Charset.defaultCharset());
    return ai.withDefaultLlm()
        .withPromptContributor(customerInput)
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
      Ai ai) throws IOException{

    var prompt = determineResolutionPlanPT.getContentAsString(Charset.defaultCharset());
    return ai.withDefaultLlm()
        .withPromptContributors(List.of(issueClassification, orderDetails))
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
      export = @Export(
          name = "inquiry",
          remote = true,
          startingInputTypes = CustomerInput.class))
  public FinalResponse resolveIssue(OrderDetails orderDetails,
                                    ResolutionPlan resolutionPlan,
                                    ResolutionConfirmation resolutionConfirmation,
                                    Ai ai) throws IOException {

    var prompt = finalResponsePT.getContentAsString(Charset.defaultCharset());
    var responseText = ai.withDefaultLlm()
        .withPromptContributors(List.of(orderDetails, resolutionPlan, resolutionConfirmation))
        .generateText(prompt);
    return new FinalResponse(responseText);
  }

}
