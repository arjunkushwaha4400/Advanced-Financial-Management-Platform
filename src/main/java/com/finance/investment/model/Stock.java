package com.finance.investment.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "stocks")
@Data
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String symbol; // e.g., AAPL

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private BigDecimal currentPrice;

    @OneToMany(mappedBy = "stock")
    private List<Investment> investments;
}
