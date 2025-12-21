package com.example.customerserviceagent.domain;

import com.embabel.common.ai.prompt.PromptContributionLocation;
import com.embabel.common.ai.prompt.PromptContributor;
import org.jetbrains.annotations.NotNull;

public record IssueClassification(
    IssueType issueType,
    float confidence) implements PromptContributor {

  @Override
  public @NotNull String contribution() {
    return "Issue type: " + issueType.name();
  }

  @Override
  public @NotNull PromptContributionLocation getPromptContributionLocation() {
    return PromptContributionLocation.BEGINNING;
  }

}
