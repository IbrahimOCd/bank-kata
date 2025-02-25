package com.skypay.bank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountTest {
    
    @Mock
    private StatementPrinter printer;
    
    private Account.Clock fixedClock;
    
    private Account account;
    
    @Captor
    private ArgumentCaptor<List<Transaction>> transactionsCaptor;
    
    private final LocalDate DATE_10_01_2012 = LocalDate.of(2012, 1, 10);
    private final LocalDate DATE_13_01_2012 = LocalDate.of(2012, 1, 13);
    private final LocalDate DATE_14_01_2012 = LocalDate.of(2012, 1, 14);

    @BeforeEach
    void setUp() {
        // Create a test clock that returns predefined dates
        fixedClock = new Account.Clock() {
            private int count = 0;
            
            @Override
            public LocalDate today() {
                return switch (count++) {
                    case 0 -> DATE_10_01_2012;
                    case 1 -> DATE_13_01_2012;
                    default -> DATE_14_01_2012;
                };
            }
        };
        
        account = new Account(printer, fixedClock);
    }

    @Test
    void should_deposit_money() {
        // When
        account.deposit(1000);
        
        // Then
        account.printStatement();
        verify(printer).print(transactionsCaptor.capture());
        
        List<Transaction> transactions = transactionsCaptor.getValue();
        assertEquals(1, transactions.size());
        assertEquals(DATE_10_01_2012, transactions.get(0).date());
        assertEquals(1000, transactions.get(0).amount());
        assertEquals(1000, transactions.get(0).balance());
    }

    @Test
    void should_withdraw_money() {
        // Given
        account.deposit(1000);
        
        // When
        account.withdraw(500);
        
        // Then
        account.printStatement();
        verify(printer).print(transactionsCaptor.capture());
        
        List<Transaction> transactions = transactionsCaptor.getValue();
        assertEquals(2, transactions.size());
        assertEquals(DATE_13_01_2012, transactions.get(1).date());
        assertEquals(-500, transactions.get(1).amount());
        assertEquals(500, transactions.get(1).balance());
    }
    
    @Test
    void should_maintain_running_balance() {
        // Given
        account.deposit(1000);
        
        // When
        account.deposit(2000);
        
        // Then
        account.printStatement();
        verify(printer).print(transactionsCaptor.capture());
        
        List<Transaction> transactions = transactionsCaptor.getValue();
        assertEquals(2, transactions.size());
        assertEquals(DATE_10_01_2012, transactions.get(0).date());
        assertEquals(1000, transactions.get(0).amount());
        assertEquals(1000, transactions.get(0).balance());
        assertEquals(DATE_13_01_2012, transactions.get(1).date());
        assertEquals(2000, transactions.get(1).amount());
        assertEquals(3000, transactions.get(1).balance());
    }
    
    @Test
    void should_print_statement_with_all_transactions() {
        // Given
        account.deposit(1000);
        account.deposit(2000);
        account.withdraw(500);
        
        // When
        account.printStatement();
        
        // Then
        verify(printer).print(transactionsCaptor.capture());
        
        List<Transaction> transactions = transactionsCaptor.getValue();
        assertEquals(3, transactions.size());
        
        assertEquals(DATE_10_01_2012, transactions.get(0).date());
        assertEquals(1000, transactions.get(0).amount());
        assertEquals(1000, transactions.get(0).balance());
        
        assertEquals(DATE_13_01_2012, transactions.get(1).date());
        assertEquals(2000, transactions.get(1).amount());
        assertEquals(3000, transactions.get(1).balance());
        
        assertEquals(DATE_14_01_2012, transactions.get(2).date());
        assertEquals(-500, transactions.get(2).amount());
        assertEquals(2500, transactions.get(2).balance());
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
}