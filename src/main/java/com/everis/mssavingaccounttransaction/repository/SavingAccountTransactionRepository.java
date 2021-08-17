package com.everis.mssavingaccounttransaction.repository;

import com.everis.mssavingaccounttransaction.entity.SavingAccountTransaction;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface SavingAccountTransactionRepository extends ReactiveMongoRepository<SavingAccountTransaction, String> {

    Flux<SavingAccountTransaction> findBySavingAccountId(String id);
}

