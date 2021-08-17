package com.everis.mssavingaccounttransaction.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@Document("SavingAccountTransaction")
@AllArgsConstructor
@NoArgsConstructor
public class SavingAccountTransaction {
    @Id
    private String id;

    @NotNull
    private SavingAccount savingAccount;

    @NotBlank
    private String transactionCode;

    @NotNull
    private TypeTransaction typeTransaction;

    @NotNull
    private Double transactionAmount;

    private Double commissionAmount;

    private LocalDateTime transactionDateTime;


}
