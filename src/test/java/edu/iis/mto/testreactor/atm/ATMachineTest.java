package edu.iis.mto.testreactor.atm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;

import edu.iis.mto.testreactor.atm.bank.AuthorizationException;
import edu.iis.mto.testreactor.atm.bank.Bank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Currency;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ATMachineTest {

    @Mock
    private Bank bank;
    private ATMachine atMachine;
    private PinCode anyPin = PinCode.createPIN(1, 2, 3, 4);
    private Card anyCard = Card.create("34985735");
    private Money anyMoney = new Money(250, Currency.getInstance("PLN"));

    @BeforeEach
    public void setUp() {
        atMachine = new ATMachine(bank, Currency.getInstance("PLN"));
    }

    @Test
    public void shouldThrowNullPointerExceptionWhenCurrencyIsNull() {
        assertThrows(NullPointerException.class, () -> new ATMachine(bank, null));
    }

    @Test
    public void shouldThrowNullPointerExceptionWhenDepositIsNull() {
        assertThrows(NullPointerException.class, () -> atMachine.setDeposit(null));
    }
    
    @Test
    public void shouldThrowATMOperationExceptionWhenCurrencyIsUnavailable() {
        List<BanknotesPack> bankPack = List.of(
                BanknotesPack.create(10, Banknote.PL_10),
                BanknotesPack.create(10, Banknote.PL_20),
                BanknotesPack.create(10, Banknote.PL_50),
                BanknotesPack.create(10, Banknote.PL_500)
        );
        MoneyDeposit defaultDeposit = MoneyDeposit.create(Currency.getInstance("PLN"), bankPack);
        atMachine.setDeposit(defaultDeposit);
        ATMOperationException atmOperationException = assertThrows(ATMOperationException.class,
                () -> atMachine.withdraw(anyPin, anyCard, anyMoney));
        assertEquals(ErrorCode.WRONG_CURRENCY, atmOperationException.getErrorCode());
    }

}
