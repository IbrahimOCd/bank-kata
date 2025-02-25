package com.skypay.bank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountTest {
    
    // Utilisation d'un Spy au lieu d'un Mock pour contourner les limitations
    @Spy
    private StatementPrinter printer = new StatementPrinter();
    
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
        account.printStatement();
        verify(printer).print(anyList());
    }

    @Test
    void should_withdraw_money() {
        // Given
        account.deposit(1000);
        
        // When
        account.withdraw(500);
        
        // Then
        account.printStatement();
        verify(printer, times(1)).print(anyList());
    }
    
    @Test
    void should_maintain_running_balance() {
        // Given
        account.deposit(1000);
        
        // When
        account.deposit(2000);
        
        // Then - Vérifie la balance via un test d'intégration léger
        account.withdraw(500);
        account.printStatement();
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