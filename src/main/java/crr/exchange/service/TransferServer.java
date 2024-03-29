package crr.exchange.service;

import com.github.sarxos.xchange.ExchangeException;
import crr.exchange.data.TransferRepository;
import crr.exchange.model.Account;
import crr.exchange.model.Transfer;
import crr.exchange.util.exception.NotEnoughFundsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
public class TransferServer {

    private final TransferRepository transferRepository;
    private final AccountServer accountServer;

    @Transactional(propagation= Propagation.NESTED, rollbackFor = Exception.class)
    public Transfer doTransfer(Transfer transfer) throws NotEnoughFundsException, ExchangeException {
        Account accountFrom = accountServer.findById(transfer.getFrom().getId());
        if (!accountFrom.isTreasury() && accountFrom.getBalance().compareTo(transfer.getAmount()) < 0) {
            throw new NotEnoughFundsException(accountFrom.getId());
        }
        Account accountTo = accountServer.findById(transfer.getTo().getId());
        transfer.setAmount(accountServer.convertCurrency(accountFrom.getCurrency(), accountTo.getCurrency(),
                transfer.getAmount()));
        accountFrom.setBalance(accountFrom.getBalance().subtract(transfer.getAmount()));
        accountTo.setBalance(accountTo.getBalance().add(transfer.getAmount()));
        accountServer.save(accountFrom);
        accountServer.save(accountTo);
        return transferRepository.save(transfer);
    }

}
