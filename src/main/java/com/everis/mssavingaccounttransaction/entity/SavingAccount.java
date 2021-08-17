package com.everis.mssavingaccounttransaction.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SavingAccount {

    String id;

    private Customer customer;

    private String cardNumber;

    private List<Person> holders;

    private List<Person> signers;

    private Integer limitTransactions;

    private Integer freeTransactions;

    private Double commissionTransactions;

    private Double balance;

    private Double minAverageVip;

    private LocalDateTime date;
}
