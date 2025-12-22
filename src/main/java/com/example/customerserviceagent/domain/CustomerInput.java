package com.example.customerserviceagent.domain;

import com.embabel.common.ai.prompt.PromptContributionLocation;
import com.embabel.common.ai.prompt.PromptContributor;
import org.jetbrains.annotations.NotNull;

public record CustomerInput(
    String text,
    String orderNumber) implements PromptContributor {

  @Override
  public @NotNull String contribution() {
    return "Customer input: " + text;
  }

  @Override
  public @NotNull PromptContributionLocation getPromptContributionLocation() {
    return PromptContributionLocation.END;
  }
}
