package com.iomp.investment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iomp.investment.dto.TransactionRequest;
import com.iomp.investment.dto.TransactionResponse;
import com.iomp.investment.service.TransactionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transactions")
@Tag(
    name = "Transactions",
    description = "APIs for managing investment transactions"
)
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(
        summary = "Create a transaction",
        description = "Creates a BUY or SELL transaction. "
                    + "The Idempotency-Key prevents the same logical "
                    + "request from being processed more than once."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description ="Transaction created or existing transaction returned for the Idempotency-Key"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid transaction data"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Portfolio or security not found"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Idempotency key conflict or concurrent update conflict"
        )
    })
    public ResponseEntity<TransactionResponse> createTransaction(
            @Valid @RequestBody TransactionRequest request,

            @Parameter(
                description = "Unique key used to make the request idempotent",
                required = true,
                example = "TXN-20260911-001"
            )
            @RequestHeader("Idempotency-Key") String idempotencyKey) {

        TransactionResponse response =
                service.createTransaction(
                        request,
                        idempotencyKey);

        return ResponseEntity.ok(response);
    }
}
