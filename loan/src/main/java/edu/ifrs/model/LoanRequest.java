package edu.ifrs.model;

public record LoanRequest(
    Long bookId,
    String borrower) {}