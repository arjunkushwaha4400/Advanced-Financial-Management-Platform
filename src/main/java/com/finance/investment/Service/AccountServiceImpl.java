package com.finance.investment.Service;

// Updated AccountServiceImpl.java
// ...
import com.finance.investment.model.Account;
import com.finance.investment.Repository.AccountRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @PreAuthorize("#account.user.username == authentication.principal.username")
    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    @Override
    @PreAuthorize("#userId == authentication.principal.id")
    public List<Account> findByUserId(Long userId) {
        return accountRepository.findByUserId(userId);
    }
}
