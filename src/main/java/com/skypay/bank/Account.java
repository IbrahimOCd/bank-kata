package com.skypay.bank;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the AccountService interface.
 * Manages bank account operations including deposits, withdrawals, 
 * and statement printing.
 */
public class Account implements AccountService {
    private final List<Transaction> transactions;
    private final StatementPrinter printer;
    private final Clock clock;
    
    /**
     * Constructs an Account with a StatementPrinter and the current system clock.
     * 
     * @param printer The statement printer to use for printing transactions
     */
    public Account(StatementPrinter printer) {
        this(printer, LocalDate::now);
    }
    
    /**
     * Constructs an Account with a StatementPrinter and a custom clock.
     * This constructor is primarily used for testing with a controlled date.
     * 
     * @param printer The statement printer to use for printing transactions
     * @param clock The clock to use for getting the current date
     */
    public Account(StatementPrinter printer, Clock clock) {
        this.transactions = new ArrayList<>();
        this.printer = printer;
        this.clock = clock;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deposit(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        recordTransaction(amount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void withdraw(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        recordTransaction(-amount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void printStatement() {
        printer.print(transactions);
    }

    /**
     * Records a transaction with the current date, amount, and calculated balance.
     * 
     * @param amount The transaction amount
     */
    private void recordTransaction(int amount) {
        int balance = calculateBalance(amount);
        transactions.add(new Transaction(clock.today(), amount, balance));
    }

    /**
     * Calculates the new balance after a transaction.
     * 
     * @param amount The amount to add to the current balance
     * @return The new balance
     */
    private int calculateBalance(int amount) {
        return transactions.isEmpty() 
                ? amount 
                : transactions.get(transactions.size() - 1).balance() + amount;
    }
    
    /**
     * Functional interface for getting the current date.
     * This allows for easier testing with fixed dates.
     */
    @FunctionalInterface
    public interface Clock {
        LocalDate today();
    }
}