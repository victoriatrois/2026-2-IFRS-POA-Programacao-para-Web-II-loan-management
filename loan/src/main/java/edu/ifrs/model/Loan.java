package edu.ifrs.model;

public record Loan(
    Long id,
    Long bookId,
    String borrower) {}