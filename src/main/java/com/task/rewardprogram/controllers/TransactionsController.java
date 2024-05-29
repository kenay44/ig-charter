package com.task.rewardprogram.controllers;

import com.task.rewardprogram.dto.TransactionDto;
import com.task.rewardprogram.dto.TransactionRequest;
import com.task.rewardprogram.services.TransactionsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionsController {

    private final TransactionsService transactionsService;

    @Autowired
    public TransactionsController(TransactionsService transactionsService) {
        this.transactionsService = transactionsService;
    }

    @PostMapping()
    @Operation(description = "Create new transaction for provided customer id and value. Transaction date-time is auto-assigned based on the current date-time")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Transaction created successfully, Location header provided"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(contentSchema = @Schema(oneOf = {List.class})))
    })
    public ResponseEntity<Void> createTransaction(@RequestBody @Valid TransactionRequest transactionRequest) {
        long transactionId = transactionsService.addTransaction(transactionRequest);
        return ResponseEntity.created(URI.create("/api/transactions/" + transactionId)).build();
    }

    @PutMapping("/{id}")
    @Operation(description = "Update existing transaction with new customer or transaction value. Transaction date-time will not be changed")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Transaction updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(contentSchema = @Schema(oneOf = {List.class}))),
            @ApiResponse(responseCode = "404", description = "Transaction to be updated not found")
    })
    public ResponseEntity<Void> updateTransaction(@RequestBody @Valid TransactionRequest transactionRequest,
                                                  @PathVariable Long id) {
        transactionsService.updateTransaction(transactionRequest, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(description = "Get transaction by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved transaction data for the provided transaction ID"),
            @ApiResponse(responseCode = "404", description = "Transaction with requested ID not found",
                    content = @Content(contentSchema = @Schema(oneOf = {Void.class})))
    })
    public ResponseEntity<TransactionDto> getTransaction(@PathVariable("id") Long id) {
        return ResponseEntity.of(transactionsService.getTransaction(id));
    }
}
