package com.skypay.bank;

import java.time.LocalDate;

/**
 * Represents a banking transaction with the date, amount, and resulting balance.
 * Implemented as a Java record for immutability and automatic getter creation.
 */
public record Transaction(LocalDate date, int amount, int balance) {
    /**
     * Creates a new transaction with the specified date, amount, and balance.
     *
     * @param date    The date of the transaction
     * @param amount  The amount of the transaction (positive for deposits, negative for withdrawals)
     * @param balance The account balance after this transaction
     */
    public Transaction {
        // Validation could be added here if needed
    }
}