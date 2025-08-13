package com.finance.investment.Repository;

import com.finance.investment.model.Investment;
import com.finance.investment.model.InvestmentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, InvestmentId> {}
