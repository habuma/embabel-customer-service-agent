package com.example.customerserviceagent.domain;

public record IssueClassification(
    IssueType issueType,
    float confidence) {}
