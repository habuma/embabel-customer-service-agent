package com.example.customerserviceagent;

import com.embabel.agent.api.annotation.*;
import com.embabel.agent.api.common.OperationContext;
import com.example.customerserviceagent.domain.*;
import com.example.customerserviceagent.order.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@Agent(name = "customerServiceAgent",
      description = "Addresses customer order issues")
public class CustomerServiceAgent {

  private static final Logger LOGGER = LoggerFactory.getLogger(CustomerServiceAgent.class);

  private final CSConfig config;

  private final OrderService orderService;

  public CustomerServiceAgent(OrderService orderService, CSConfig config) {
    this.orderService = orderService;
    this.config = config;
  }

  @Action(description = "Classifies the issue based on customer input")
  public IssueClassification classifyIssue(CustomerInput customerInput, OperationContext context) throws IOException {
    LOGGER.info("Classifying customer input");
    var prompt = """
        Based on the customer's input, classify the issue as one of the following:
        MISSING_ITEM, DAMAGED_ITEM, WRONG_ITEM, DELAYED, REFUND_REQUEST, or OTHER.
        """;
    return config.getCustomerService().promptRunner(context)
        .withPromptContributor(customerInput)
        .createObject(prompt, IssueClassification.class);
  }

  @Action(description = "Gets the order details")
  public OrderDetails checkOrderStatus(CustomerInput customerInput) {
    LOGGER.info("Checking order details");
    String orderNumber = customerInput.orderNumber();
    if (orderNumber == null) {
      OrderNumberInput orderNumberInput = WaitFor.formSubmission("What is the order ID?", OrderNumberInput.class);
      orderNumber = orderNumberInput.orderNumber();
    }

    return orderService.getOrderDetails(orderNumber);
  }

  @Action(description = "Determine resolution plan")
  public ResolutionPlan determineResolutionPlan(
      IssueClassification issueClassification,
      OrderDetails orderDetails,
      OperationContext context) throws IOException {
    LOGGER.info("Determining resolution plan");

    var prompt = """
        Given the issue classification and order details, determine a resolution plan from
        one of the following: REFUND, RESEND_ITEM, CONTACT_CUSTOMER
        """;
    return config.getCustomerService().promptRunner(context)
        .withPromptContributors(List.of(issueClassification, orderDetails))
        .createObject(prompt, ResolutionPlan.class);
  }

  @Action(description = "Execute resolution")
  public ResolutionConfirmation executeResolution(OrderDetails orderDetails, ResolutionPlan resolutionPlan) {
    LOGGER.info("Execute resolution");
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
                                    OperationContext context) throws IOException {
    LOGGER.info("Resolving issue");

    var prompt = """
        Generate a final response to the customer including a summary of the resolution details.
        """;
    var responseText = config.getCustomerService().promptRunner(context)
        .withPromptContributors(List.of(orderDetails, resolutionPlan, resolutionConfirmation))
        .generateText(prompt);
    return new FinalResponse(responseText);
  }

}
