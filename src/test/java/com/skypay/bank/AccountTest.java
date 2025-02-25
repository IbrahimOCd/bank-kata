package com.skypay.bank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {
    
    private TestStatementPrinter printer;
    private Account account;
    private static final LocalDate TODAY = LocalDate.of(2012, 1, 14);
    
    @BeforeEach
    void setUp() {
        printer = new TestStatementPrinter();
        account = new Account(printer, () -> TODAY);
    }

    @Test
    void should_deposit_money() {
        // When
        account.deposit(1000);
        
        // Then
        account.printStatement();
        assertTrue(printer.isPrintCalled());
        assertEquals(1, printer.getTransactions().size());
        assertEquals(1000, printer.getTransactions().get(0).amount());
    }

    @Test
    void should_withdraw_money() {
        // Given
        account.deposit(1000);
        printer.reset();
        
        // When
        account.withdraw(500);
        
        // Then
        account.printStatement();
        assertTrue(printer.isPrintCalled());
        assertEquals(2, printer.getTransactions().size());
        assertEquals(-500, printer.getTransactions().get(1).amount());
    }
    
    @Test
    void should_throw_exception_on_negative_deposit() {
        assertThrows(IllegalArgumentException.class, () -> account.deposit(-100));
    }
    
    @Test
    void should_throw_exception_on_negative_withdrawal() {
        assertThrows(IllegalArgumentException.class, () -> account.withdraw(-100));
    }
    
    @Test
    void should_throw_exception_on_zero_deposit() {
        assertThrows(IllegalArgumentException.class, () -> account.deposit(0));
    }
    
    @Test
    void should_throw_exception_on_zero_withdrawal() {
        assertThrows(IllegalArgumentException.class, () -> account.withdraw(0));
    }
    
    private static class TestStatementPrinter extends StatementPrinter {
        private boolean printCalled = false;
        private List<Transaction> lastTransactions;
        
        @Override
        public void print(List<Transaction> transactions) {
            this.printCalled = true;
            this.lastTransactions = transactions;
            super.print(transactions);
        }
        
        public void reset() {
            this.printCalled = false;
            this.lastTransactions = null;
        }
        
        public boolean isPrintCalled() {
            return printCalled;
        }
        
        public List<Transaction> getTransactions() {
            return lastTransactions;
        }
    }
}