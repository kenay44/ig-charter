package com.task.rewardprogram.controllers;

import com.task.rewardprogram.dto.TransactionRequest;
import com.task.rewardprogram.entities.Transaction;
import com.task.rewardprogram.jpa.TransactionsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.Rollback;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransactionsControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TransactionsRepository transactionsRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void cleanTransactions() {
        transactionsRepository.deleteAll();
    }

    @Test
    @Rollback
    void postTransactionShouldReturnStatusOKWhenTransactionCreated() {
        //given
        List<Transaction> transactions = transactionsRepository.findAll();
        assertThat(transactions).isEmpty();

        Long customerId = 1L;

        //when
        ResponseEntity<Void> response = this.restTemplate
                .postForEntity("http://localhost:"+port+"/api/transactions",
                        new TransactionRequest(customerId, BigDecimal.valueOf(100)),
                        Void.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().get(HttpHeaders.LOCATION)).isNotNull();
        assertThat(Objects.requireNonNull(response.getHeaders().get(HttpHeaders.LOCATION)).size()).isEqualTo(1);
        assertThat(Objects.requireNonNull(response.getHeaders().get(HttpHeaders.LOCATION)).get(0)).startsWith("/api/transactions/");

        URI location = URI.create(Objects.requireNonNull(response.getHeaders().get(HttpHeaders.LOCATION)).get(0));
        long transactionId = Long.parseLong(location.getPath().substring(location.getPath().lastIndexOf("/") +1));

        assertThat(transactionsRepository.findById(transactionId)).isPresent();
    }

    @Test
    void putTransactionShouldReturnStatusOKWhenTransactionUpdated() {
        //given
        long customerId = 1L;
        Transaction transaction = new Transaction();
        transaction.setCustomerId(customerId);
        transaction.setTransactionValue(BigDecimal.valueOf(100));
        transaction.setTransactionDate(LocalDateTime.now());

        transactionsRepository.save(transaction);

        //when
        ResponseEntity<Void> response = this.restTemplate
                .exchange(URI.create("http://localhost:"+port+"/api/transactions/"+transaction.getId()),
                        HttpMethod.PUT,
                        new HttpEntity<>(new TransactionRequest(customerId, BigDecimal.valueOf(200))),
                        Void.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        //and transaction value changed
        assertThat(transactionsRepository.findById(transaction.getId())).isNotEmpty()
                .map(Transaction::getTransactionValue).contains(BigDecimal.valueOf(20000, 2));
    }

    @Test
    void putTransactionShouldReturnStatusBadRequestWhenCustomerNotFound() {
        //given
        long notExistingTransactionId = 10L;

        //when
        ResponseEntity<?> response = restTemplate
                .exchange(URI.create("http://localhost:" + port + "/api/transactions/" + notExistingTransactionId),
                        HttpMethod.PUT,
                        new HttpEntity<>(new TransactionRequest(10L, BigDecimal.valueOf(200))),
                        List.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
