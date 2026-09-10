package com.iomp.investment.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iomp.investment.dto.SecurityRequest;
import com.iomp.investment.dto.SecurityResponse;
import com.iomp.investment.service.SecurityService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/securities")
@Tag(
    name = "Securities",
    description = "APIs for managing investment securities"
)
public class SecurityController {

    private final SecurityService service;

    public SecurityController(SecurityService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(
        summary = "Create a security",
        description = "Creates a new investment security"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Security created successfully"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid security data"
        )
    })
    public ResponseEntity<SecurityResponse> createSecurity(
            @Valid @RequestBody SecurityRequest request) {

        SecurityResponse response =
                service.createSecurity(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    @Operation(
        summary = "Get all securities",
        description = "Retrieves all available investment securities"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Securities retrieved successfully"
    )
    public List<SecurityResponse> fetchSecurities() {

        return service.getSecurities();
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get security by ID",
        description = "Retrieves a security using its unique ID"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Security retrieved successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Security not found"
        )
    })
    public SecurityResponse fetchSecurityById(
            @PathVariable long id) {

        return service.getSecurityById(id);
    }
}
