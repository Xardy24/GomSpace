package crr.exchange.service;


import com.github.sarxos.xchange.ExchangeException;
import crr.exchange.data.TransferRepository;
import crr.exchange.model.Account;
import crr.exchange.model.Transfer;
import crr.exchange.util.exception.NotEnoughFundsException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferServerTest {

    private TransferServer transferServer;

    @Mock
    private TransferRepository transferRepository;

    @Mock
    private AccountServer accountServer;

    private static Account from;
    private static Account to;
    private static Transfer transfer;

    @BeforeAll
    static void initializeData() {
        from = new Account();
        from.setId(1L);
        from.setName("from");
        from.setCurrency(Currency.getInstance("USD"));
        to = new Account();
        to.setId(2L);
        to.setName("to");
        to.setCurrency(Currency.getInstance("USD"));
        transfer = new Transfer();
        transfer.setId(1L);
        transfer.setFrom(from);
        transfer.setTo(to);
        transfer.setAmount(BigDecimal.ONE);
    }

    @BeforeEach
    void initTransferServer() {
        transferServer = new TransferServer(transferRepository, accountServer);
    }

    @Test
    void doTransfer_whenItIsEnoughFunds_doTheTransfer() throws ExchangeException {
        // given
        from.setTreasury(false);
        from.setBalance(BigDecimal.ONE);
        to.setTreasury(false);
        to.setBalance(BigDecimal.ONE);
        BigDecimal converted = new BigDecimal(1);
        when(accountServer.findById(from.getId())).thenReturn(from);
        when(accountServer.findById(to.getId())).thenReturn(to);
        when(accountServer.save(any(Account.class))).then(returnsFirstArg());
        when(accountServer.convertCurrency(any(Currency.class),any(Currency.class), any(BigDecimal.class))).thenReturn(converted);
        when(transferRepository.save(any(Transfer.class))).then(returnsFirstArg());
        // when
        transferServer.doTransfer(transfer);
        // then
        assertThat(from.getBalance()).isEqualTo(BigDecimal.ZERO);
        assertThat(to.getBalance()).isEqualTo(BigDecimal.valueOf(2));
    }

    @Test
    void doTransfer_whenItIsNotEnoughFundsWithNoTreasury_abortTheTransfer() {
        // given
        from.setTreasury(false);
        from.setBalance(BigDecimal.ZERO);
        to.setTreasury(false);
        to.setBalance(BigDecimal.ONE);
        when(accountServer.findById(from.getId())).thenReturn(from);
        // when
        try {
            transferServer.doTransfer(transfer);
        }
        // then
        catch (NotEnoughFundsException | ExchangeException e) {
            assertThat(from.getBalance()).isEqualTo(BigDecimal.ZERO);
            assertThat(to.getBalance()).isEqualTo(BigDecimal.ONE);
            assertThat(e.getMessage()).isEqualTo("Not enough funds in no-treasury account with id=1. The transfer has been aborted.");
        }
    }

    @Test
    void doTransfer_whenItIsNotEnoughFundsWithTreasury_doTheTransfer() throws ExchangeException {
        // given
        from.setTreasury(true);
        from.setBalance(BigDecimal.ZERO);
        to.setTreasury(false);
        to.setBalance(BigDecimal.ONE);
        BigDecimal converted = new BigDecimal(1);
        when(accountServer.findById(from.getId())).thenReturn(from);
        when(accountServer.findById(to.getId())).thenReturn(to);
        when(accountServer.save(any(Account.class))).then(returnsFirstArg());
        when(accountServer.convertCurrency(any(Currency.class),any(Currency.class), any(BigDecimal.class))).thenReturn(converted);
        when(transferRepository.save(any(Transfer.class))).then(returnsFirstArg());
        // when
        transferServer.doTransfer(transfer);
        // then
        assertThat(from.getBalance()).isEqualTo(BigDecimal.valueOf(-1));
        assertThat(to.getBalance()).isEqualTo(BigDecimal.valueOf(2));
    }
}