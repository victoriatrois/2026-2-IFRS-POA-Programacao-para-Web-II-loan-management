package edu.ifrs.model;

public record Book(
    Long id,
    String title,
    String author,
    boolean loaned
) {}