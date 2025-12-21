package com.example.customerserviceagent.domain;

import com.embabel.common.ai.prompt.PromptContributionLocation;
import com.embabel.common.ai.prompt.PromptContributor;
import org.jetbrains.annotations.NotNull;

public record ResolutionConfirmation(String id) implements PromptContributor {

  @Override
  public @NotNull String contribution() {
    return "Resolution Confirmation ID: " + id;
  }

  @Override
  public @NotNull PromptContributionLocation getPromptContributionLocation() {
    return PromptContributionLocation.BEGINNING;
  }

}
