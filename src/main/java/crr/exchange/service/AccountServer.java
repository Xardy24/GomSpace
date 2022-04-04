package crr.exchange.service;

import com.github.sarxos.xchange.ExchangeCache;
import com.github.sarxos.xchange.ExchangeException;
import com.github.sarxos.xchange.ExchangeRate;
import crr.exchange.data.AccountRepository;
import crr.exchange.model.Account;
import lombok.RequiredArgsConstructor;
import crr.exchange.util.exception.EntityNotFoundByIdException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AccountServer {

    private final AccountRepository accountRepository;

    @Value("${apikey}")
    // Change apikey in properties for the correct one
    protected String apikey;

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public Account findById(Long accountId) throws EntityNotFoundByIdException {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundByIdException(accountId, Account.class.getSimpleName()));
    }

    public Account save(Account account) {
        return accountRepository.save(account);
    }

    public Account editById(Long accountId, Account accountData) { // do not modify treasury field
        Account editedAccount = this.findById(accountId);
        if (accountData.getBalance() != null) editedAccount.setBalance(accountData.getBalance());
        if (accountData.getCurrency() != null) editedAccount.setCurrency(accountData.getCurrency());
        if (accountData.getName() != null) editedAccount.setName(accountData.getName());
        return accountRepository.save(editedAccount);
    }

    public BigDecimal convertCurrency(Currency baseCurrency, Currency newCurrency, BigDecimal amountToConvert) throws ExchangeException {
        ExchangeCache.setParameter("openexchangerates.org.apikey", apikey);
        // define base currency
        ExchangeCache cache = new ExchangeCache(baseCurrency.getCurrencyCode());
        // get the CAD to USD exchange rate
        ExchangeRate rate = cache.getRate(newCurrency.getCurrencyCode());
        // convert
        return rate.convert(new BigDecimal(amountToConvert.toString()));
    }

}
