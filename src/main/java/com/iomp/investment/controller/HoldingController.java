package com.iomp.investment.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iomp.investment.dto.HoldingRequest;
import com.iomp.investment.dto.HoldingResponse;
import com.iomp.investment.service.HoldingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/holdings")
@Tag(
    name = "Holdings",
    description = "APIs for managing portfolio holdings"
)
public class HoldingController {

    private final HoldingService service;

    public HoldingController(HoldingService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(
        summary = "Add or update a holding",
        description = "Adds a security to a portfolio or updates the "
                    + "existing holding quantity"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Holding added or updated successfully"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid holding data"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Portfolio or security not found"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Concurrent holding update conflict"
        )
    })
    public HoldingResponse addHolding(
            @Valid @RequestBody HoldingRequest request) {

        return service.addHolding(request);
    }
}
