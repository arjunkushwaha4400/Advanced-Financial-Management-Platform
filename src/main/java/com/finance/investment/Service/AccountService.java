package com.finance.investment.Service;

// Updated AccountService.java
import com.finance.investment.model.Account;
import java.util.List;

public interface AccountService {
    Account createAccount(Account account);
    List<Account> findByUserId(Long userId);
}
