package com.example.customerserviceagent.domain;

public record ResolutionPlan(
    ResolutionType resolutionType,
    String reason) {}
