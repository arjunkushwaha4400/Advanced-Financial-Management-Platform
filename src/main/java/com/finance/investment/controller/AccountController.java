package com.finance.investment.controller;

// AccountController.java

import com.finance.investment.model.Account;
import com.finance.investment.model.User;
import com.finance.investment.Service.AccountService;
import com.finance.investment.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;
    private final UserService userService;

    public AccountController(AccountService accountService, UserService userService) {
        this.accountService = accountService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody Account account, @AuthenticationPrincipal UserDetails currentUser) {
        Optional<User> users = userService.findByUsername(currentUser.getUsername());
        if(!users.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        User user = users.get();
        account.setUser(user);
        Account newAccount = accountService.createAccount(account);
        return new ResponseEntity<>(newAccount, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Account>> getUserAccounts(@AuthenticationPrincipal UserDetails currentUser) {
        Optional<User> users = userService.findByUsername(currentUser.getUsername());
        if(!users.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        User user = users.get();
        List<Account> accounts = accountService.findByUserId(user.getId());
        return ResponseEntity.ok(accounts);
    }
}
