package com.everis.mssavingaccounttransaction.controller;

import com.everis.mssavingaccounttransaction.entity.SavingAccountTransaction;
import com.everis.mssavingaccounttransaction.service.SavingAccountTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/transactionSavingAccount")
public class TransactionSavingAccountController {
    @Autowired
    SavingAccountTransactionService savingAccountTransactionService;

    @GetMapping("list")
    public Flux<SavingAccountTransaction> findAll(){
        return savingAccountTransactionService.findAll();
    }

    @GetMapping("/find/{id}")
    public Mono<SavingAccountTransaction> findById(@PathVariable String id){
        return savingAccountTransactionService.findById(id);
    }

    @PostMapping("/create")
    public Mono<ResponseEntity<SavingAccountTransaction>> create(@RequestBody SavingAccountTransaction savingAccountTransaction){

       return savingAccountTransactionService.countMovements(savingAccountTransaction.getSavingAccount().getId()) // N° Movimientos actuales
               .flatMap(cnt -> {
                   return savingAccountTransactionService.findSavingAccountById(savingAccountTransaction.getSavingAccount().getId()) // Cuenta Bancaria
                           .filter(sa -> sa.getLimitTransactions() > cnt)
                           .flatMap(sa -> {
                               switch (savingAccountTransaction.getTypeTransaction()){
                                   case DEPOSIT: sa.setBalance(sa.getBalance() + savingAccountTransaction.getTransactionAmount()); break;
                                   case DRAFT: sa.setBalance(sa.getBalance() - savingAccountTransaction.getTransactionAmount()); break;
                               }
                               if(cnt >= sa.getFreeTransactions() ){
                                   sa.setBalance(sa.getBalance() - sa.getCommissionTransactions());
                                   savingAccountTransaction.setCommissionAmount(sa.getCommissionTransactions());
                               }else{
                                   savingAccountTransaction.setCommissionAmount(0.0);
                               }

                               return savingAccountTransactionService.updateSavingAccount(sa)
                                       .flatMap(saveAcc -> {
                                           savingAccountTransaction.setSavingAccount(saveAcc);
                                           savingAccountTransaction.setTransactionDateTime(LocalDateTime.now());
                                           return savingAccountTransactionService.create(savingAccountTransaction);
                                       });
                           })

                           .map(sat ->new ResponseEntity<>(sat , HttpStatus.CREATED) );
               })
               .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));





//            return savingAccountTransactionService.create(c)
//                    .map(savedCustomer -> new ResponseEntity<>(savedCustomer , HttpStatus.CREATED));
    }

    @PutMapping("/update")
    public Mono<ResponseEntity<SavingAccountTransaction>> update(@RequestBody SavingAccountTransaction transaction) {

        return savingAccountTransactionService.findSavingAccountById(transaction.getSavingAccount().getId())
                .flatMap(sa -> {
                    return savingAccountTransactionService.findById(transaction.getId())
                            .flatMap(sat -> {
                                switch (transaction.getTypeTransaction()){
                                    case DEPOSIT: sa.setBalance(sa.getBalance() - sat.getTransactionAmount() + transaction.getTransactionAmount());
                                        return savingAccountTransactionService.updateSavingAccount(sa).flatMap(saUpdate -> {
                                                                                            transaction.setSavingAccount(saUpdate);
                                                                                            transaction.setTransactionDateTime(LocalDateTime.now());
                                                                                            return savingAccountTransactionService.update(transaction);
                                                                                        });
                                    case DRAFT: sa.setBalance(sa.getBalance() + sat.getTransactionAmount() - transaction.getTransactionAmount());
                                        return savingAccountTransactionService.updateSavingAccount(sa).flatMap(saUpdate -> {
                                                                                                transaction.setSavingAccount(saUpdate);
                                                                                                transaction.setTransactionDateTime(LocalDateTime.now());
                                                                                                return savingAccountTransactionService.update(transaction);
                                                                                            });
                                    default: return Mono.empty();
                                }

                            });
                })
                .map(sat ->new ResponseEntity<>(sat , HttpStatus.CREATED))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

    }

    @DeleteMapping("/delete/{id}")
    public Mono<ResponseEntity<String>> delete(@PathVariable String id) {
        return savingAccountTransactionService.delete(id)
                .filter(deleteCustomer -> deleteCustomer)
                .map(deleteCustomer -> new ResponseEntity<>("Customer Deleted", HttpStatus.ACCEPTED))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
