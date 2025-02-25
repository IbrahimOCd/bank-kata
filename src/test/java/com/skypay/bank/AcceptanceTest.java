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
        Account.Clock fixedClock = mockClock();
        StatementPrinter printer = new StatementPrinter();
        Account account = new Account(printer, fixedClock);
        
        // Whens
        account.deposit(1000);
        account.deposit(2000);
        account.withdraw(500);
        account.printStatement();
        
        // Then
        String expectedOutput = """
                Date || Amount || Balance
                14/01/2012 || -500 || 2500
                13/01/2012 || 2000 || 3000
                10/01/2012 || 1000 || 1000
                """.replaceAll("\\R", System.lineSeparator());
        
        assertEquals(expectedOutput, outContent.toString());
    }
    
    private Account.Clock mockClock() {
        return new Account.Clock() {
            private int count = 0;
            
            @Override
            public LocalDate today() {
                return switch (count++) {
                    case 0 -> LocalDate.of(2012, 1, 10);
                    case 1 -> LocalDate.of(2012, 1, 13);
                    default -> LocalDate.of(2012, 1, 14);
                };
            }
        };
    }
}