package com.finance.investment.Service;

import com.finance.investment.Repository.AccountRepository;
import org.springframework.stereotype.Service;

@Service("ownershipChecker")
public class AccountOwnershipChecker {
    private final AccountRepository accountRepository;

    public AccountOwnershipChecker(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public boolean isAccountOwner(Long accountId, String username) {
        return accountRepository.findById(accountId)
                .map(account -> account.getUser().getUsername().equals(username))
                .orElse(false);
    }
}
