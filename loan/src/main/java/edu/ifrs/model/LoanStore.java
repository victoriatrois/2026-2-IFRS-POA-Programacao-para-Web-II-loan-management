package edu.ifrs.model;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;

import java.util.List;

@ApplicationScoped
public class LoanStore {
  private final List<Loan> loans = new ArrayList<>();
  private long nextId = 1L;

  public Loan add(LoanRequest request) {
    var created = new Loan(nextId++, request.bookId(), request.borrower());
    loans.add(created);
    return created;
  }

  public List<Loan> list() {
    return List.copyOf(loans);
  }

  public void reset() {
    loans.clear();
    nextId = 1L;
  }
}
