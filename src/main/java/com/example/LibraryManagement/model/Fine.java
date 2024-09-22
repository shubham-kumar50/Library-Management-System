package com.example.LibraryManagement.model;

public class Fine {
    private int userId;
    private int bookId;
    private double amount;

    public Fine(int userId, int bookId, double amount) {
        this.userId = userId;
        this.bookId = bookId;
        this.amount = amount;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
