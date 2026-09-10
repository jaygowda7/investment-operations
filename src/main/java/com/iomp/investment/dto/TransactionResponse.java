package com.iomp.investment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.iomp.investment.enums.TransactionType;

import lombok.Data;

@Data
public class TransactionResponse {

    private Long id;
    private Long portfolioId;
    private Long securityId;
    private TransactionType transactionType;
    private BigDecimal quantity;
    private LocalDateTime transactionDateTime;
}
