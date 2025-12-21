package com.example.customerserviceagent.domain;

import com.embabel.common.ai.prompt.PromptContributionLocation;
import com.embabel.common.ai.prompt.PromptContributor;
import org.jetbrains.annotations.NotNull;

public record ResolutionPlan(
    ResolutionType resolutionType,
    String reason) implements PromptContributor {

  @Override
  public @NotNull String contribution() {
    return "Resolution Type: " + resolutionType + "\nReason: " + reason;
  }

  @Override
  public @NotNull PromptContributionLocation getPromptContributionLocation() {
    return PromptContributionLocation.BEGINNING;
  }

}
