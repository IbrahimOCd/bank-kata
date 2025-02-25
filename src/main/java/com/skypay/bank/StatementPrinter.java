package com.skypay.bank;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Responsible for printing account statements from a list of transactions.
 */
public class StatementPrinter {
    private static final String HEADER = "Date || Amount || Balance";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    /**
     * Prints a formatted statement of all transactions.
     * Transactions are displayed in reverse chronological order (newest first).
     *
     * @param transactions The list of transactions to print
     */
    public void print(List<Transaction> transactions) {
        System.out.println(HEADER);
        
        // Print transactions in reverse order (newest first)
        transactions.stream()
                .sorted((t1, t2) -> t2.date().compareTo(t1.date()))
                .forEach(this::printLine);
    }

    /**
     * Prints a single transaction line.
     *
     * @param transaction The transaction to print
     */
    private void printLine(Transaction transaction) {
        System.out.printf("%s || %s || %d%n",
                formatDate(transaction.date()),
                formatAmount(transaction.amount()),
                transaction.balance());
    }

    /**
     * Formats the transaction date.
     *
     * @param date The date to format
     * @return Formatted date string
     */
    private String formatDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }
    
    /**
     * Formats the transaction amount.
     *
     * @param amount The amount to format
     * @return Formatted amount string
     */
    private String formatAmount(int amount) {
        return amount > 0 ? String.valueOf(amount) : String.valueOf(amount);
    }
}