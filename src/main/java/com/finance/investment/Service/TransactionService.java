package com.finance.investment.Service;

// TransactionService.java
import com.finance.investment.model.Account;
import com.finance.investment.model.Transaction;
import com.finance.investment.Repository.AccountRepository;
import com.finance.investment.Repository.TransactionRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    @PreAuthorize("@ownershipChecker.isAccountOwner(#accountId, authentication.principal.username)")
    public Transaction createTransaction(Long accountId, Transaction transaction) {
        // Find the account, or throw an exception if not found
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Update the account balance
        account.setBalance(account.getBalance().add(transaction.getAmount()));
        accountRepository.save(account);

        // Link the transaction to the account and save it
        transaction.setAccount(account);
        return transactionRepository.save(transaction);
    }

    @PreAuthorize("@ownershipChecker.isAccountOwner(#accountId, authentication.principal.username)")
    public List<Transaction> getTransactionsByAccountId(Long accountId) {
        // ... (existing logic to find and return transactions) ...
        return transactionRepository.findByAccountId(accountId);
    }
}
