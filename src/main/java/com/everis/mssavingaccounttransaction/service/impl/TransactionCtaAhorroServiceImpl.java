package com.everis.mssavingaccounttransaction.service.impl;

import com.everis.mssavingaccounttransaction.entity.SavingAccount;
import com.everis.mssavingaccounttransaction.entity.SavingAccountTransaction;
import com.everis.mssavingaccounttransaction.repository.SavingAccountTransactionRepository;
import com.everis.mssavingaccounttransaction.service.SavingAccountTransactionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TransactionCtaAhorroServiceImpl implements SavingAccountTransactionService {

	private final WebClient webClient;
	private final ReactiveCircuitBreaker reactiveCircuitBreaker;
	
	String uri = "http://localhost:8090/api/ms-saving-account/savingAccount";

	public TransactionCtaAhorroServiceImpl(ReactiveResilience4JCircuitBreakerFactory circuitBreakerFactory) {
		this.webClient = WebClient.builder().baseUrl(this.uri).build();
		this.reactiveCircuitBreaker = circuitBreakerFactory.create("customer");
	}
    
    @Autowired
    private SavingAccountTransactionRepository savingAccountTransactionRepository;

    // PLAN A - SavingAccountFindBy
    @Override
    public Mono<SavingAccount> findSavingAccountById(String id) {
		return reactiveCircuitBreaker.run(webClient.get().uri(this.uri + "/find/{id}",id).accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(SavingAccount.class),
				throwable -> {
					return this.getDefaultSavingAccount();
				});
    }
    
  	// Plan A - SavingAccountUpdate
    @Override
    public Mono<SavingAccount> updateSavingAccount(SavingAccount sa) {
		return reactiveCircuitBreaker.run(webClient.put().uri(this.uri + "/update",sa).accept(MediaType.APPLICATION_JSON).syncBody(sa).retrieve().bodyToMono(SavingAccount.class),
				throwable -> {
					return this.getDefaultSavingAccount();
				});
    }
    
    
    // Plan B
  	public Mono<SavingAccount> getDefaultSavingAccount() {
  		Mono<SavingAccount> savingAccount = Mono.just(new SavingAccount("0", null, null,null,null,null,null,null, null, null,null));
  		return savingAccount;
  	}
    
    @Override
    public Mono<SavingAccountTransaction> create(SavingAccountTransaction t) {
        return savingAccountTransactionRepository.save(t);
    }

    @Override
    public Flux<SavingAccountTransaction> findAll() {
        return savingAccountTransactionRepository.findAll();
    }

    @Override
    public Mono<SavingAccountTransaction> findById(String id) {
        return savingAccountTransactionRepository.findById(id);
    }

    @Override
    public Mono<SavingAccountTransaction> update(SavingAccountTransaction t) {
        return savingAccountTransactionRepository.save(t);
    }

    @Override
    public Mono<Boolean> delete(String t) {
        return savingAccountTransactionRepository.findById(t)
                .flatMap(tar -> savingAccountTransactionRepository.delete(tar).then(Mono.just(Boolean.TRUE)))
                .defaultIfEmpty(Boolean.FALSE);
    }

    @Override
    public Mono<Long> countMovements(String t) {
        return savingAccountTransactionRepository.findBySavingAccountId(t).count();
    }

}
