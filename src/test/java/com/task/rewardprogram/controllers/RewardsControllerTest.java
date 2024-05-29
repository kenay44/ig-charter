package com.task.rewardprogram.controllers;


import com.task.rewardprogram.dto.CustomerPoints;
import com.task.rewardprogram.entities.Transaction;
import com.task.rewardprogram.jpa.TransactionsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RewardsControllerTest {

    private static final LocalDateTime transactionDate = LocalDateTime.of(2024, 4, 20, 10, 10);
    private static final YearMonth pointsCalculationMonth = YearMonth.from(transactionDate);

    @LocalServerPort
    private int port;
    @Autowired
    private TransactionsRepository transactionsRepository;
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void getRewardsForCustomer() {
        //given
        long customerId = 1;
        prepareTransaction(customerId);

        //when
        ResponseEntity<List<CustomerPoints>> response = this.restTemplate
                .exchange("http://localhost:"+port+"/api/rewards?customerId="+customerId+"&endMonth="+ pointsCalculationMonth,
                        HttpMethod.GET, null, new ParameterizedTypeReference<>(){});
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(expectedCustomerPoints(customerId));
    }

    @Test
    void getRewardsForNotExistingCustomer() {
        //when
        ResponseEntity<List<CustomerPoints>> response = this.restTemplate
                .exchange("http://localhost:"+port+ "/api/rewards?customerId=22&endMonth=" + pointsCalculationMonth,
                        HttpMethod.GET, null, new ParameterizedTypeReference<>(){});

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody())).isEqualTo(List.of());
    }

    private List<CustomerPoints> expectedCustomerPoints(long customerId) {
        return List.of(
                new CustomerPoints(customerId, Map.of(YearMonth.from(transactionDate), 70L), 70L)
        );
    }

    private void prepareTransaction(long customerId) {
        Transaction transaction = new Transaction();
        transaction.setCustomerId(customerId);
        transaction.setTransactionValue(BigDecimal.valueOf(110));
        transaction.setTransactionDate(transactionDate);
        transactionsRepository.save(transaction);
    }
}
