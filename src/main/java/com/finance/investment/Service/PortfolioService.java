package com.finance.investment.Service;

import com.finance.investment.model.Portfolio;
import com.finance.investment.Repository.PortfolioRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final AccountOwnershipChecker ownershipChecker; // Reusing our custom checker

    public PortfolioService(PortfolioRepository portfolioRepository, AccountOwnershipChecker ownershipChecker) {
        this.portfolioRepository = portfolioRepository;
        this.ownershipChecker = ownershipChecker;
    }

    @PreAuthorize("#portfolio.user.username == authentication.principal.username")
    public Portfolio createPortfolio(Portfolio portfolio) {
        return portfolioRepository.save(portfolio);
    }

    @PreAuthorize("#userId == authentication.principal.id")
    public List<Portfolio> findByUserId(Long userId) {
        return portfolioRepository.findByUserId(userId);
    }
}
