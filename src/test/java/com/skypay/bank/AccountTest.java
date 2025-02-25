package com.skypay.bank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
    
    @Mock
    private Account.Clock clock;
    
    private Account account;
    private final LocalDate TODAY = LocalDate.of(2012, 1, 14);

    @BeforeEach
    void setUp() {
        when(clock.today()).thenReturn(TODAY);
        account = new Account(printer, clock);
    }

    @Test
    void should_deposit_money() {
        // When
        account.deposit(1000);
        
        // Then
        ArgumentCaptor<List<Transaction>> captor = ArgumentCaptor.forClass(List.class);
        account.printStatement();
        verify(printer).print(captor.capture());
        
        List<Transaction> transactions = captor.getValue();
        assertEquals(1, transactions.size());
        assertEquals(TODAY, transactions.get(0).date());
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
        ArgumentCaptor<List<Transaction>> captor = ArgumentCaptor.forClass(List.class);
        account.printStatement();
        verify(printer).print(captor.capture());
        
        List<Transaction> transactions = captor.getValue();
        assertEquals(2, transactions.size());
        assertEquals(TODAY, transactions.get(1).date());
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
        ArgumentCaptor<List<Transaction>> captor = ArgumentCaptor.forClass(List.class);
        account.printStatement();
        verify(printer).print(captor.capture());
        
        List<Transaction> transactions = captor.getValue();
        assertEquals(2, transactions.size());
        assertEquals(3000, transactions.get(1).balance());
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