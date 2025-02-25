package com.skypay.bank;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AcceptanceTest {
    
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    
    @BeforeEach
    void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }
    
    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }
    
    @Test
    void should_print_statement_after_multiple_transactions() {
        // Given
        TestClock fixedClock = new TestClock();
        StatementPrinter printer = new StatementPrinter();
        Account account = new Account(printer, fixedClock);
        
        // When
        account.deposit(1000);
        account.deposit(2000);
        account.withdraw(500);
        account.printStatement();
        
        // Then
        String expectedOutput = "Date || Amount || Balance\n" +
                                "14/01/2012 || -500 || 2500\n" +
                                "13/01/2012 || 2000 || 3000\n" +
                                "10/01/2012 || 1000 || 1000\n";
        
        // Normaliser les sauts de ligne pour la comparaison
        String normalizedExpected = expectedOutput.replaceAll("\\R", System.lineSeparator());
        String normalizedActual = outContent.toString().replaceAll("\\R", System.lineSeparator());
        
        assertEquals(normalizedExpected, normalizedActual);
    }
    
    // Classe interne pour simuler une horloge avec des dates prédéfinies
    private static class TestClock implements Account.Clock {
        private int count = 0;
        
        @Override
        public LocalDate today() {
            return switch (count++) {
                case 0 -> LocalDate.of(2012, 1, 10);
                case 1 -> LocalDate.of(2012, 1, 13);
                default -> LocalDate.of(2012, 1, 14);
            };
        }
    }
}