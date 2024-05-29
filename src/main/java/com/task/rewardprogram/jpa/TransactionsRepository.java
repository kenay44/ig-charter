package com.task.rewardprogram.jpa;

import com.task.rewardprogram.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionsRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByTransactionDateBetween(LocalDateTime start, LocalDateTime end);

    List<Transaction> findAllByCustomerIdAndTransactionDateBetween(Long customerId, LocalDateTime start, LocalDateTime end);

}
