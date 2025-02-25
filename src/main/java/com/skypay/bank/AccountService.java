package com.skypay.bank;

/**
 * Interface defining the banking operations.
 * This is the public interface that clients will use.
 */
public interface AccountService {
    /**
     * Makes a deposit to the account.
     *
     * @param amount The amount to deposit (positive integer)
     */
    void deposit(int amount);

    /**
     * Makes a withdrawal from the account.
     *
     * @param amount The amount to withdraw (positive integer)
     */
    void withdraw(int amount);

    /**
     * Prints a statement of all transactions for this account.
     */
    void printStatement();
}