package com.everis.mssavingaccounttransaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class MsSavingAccountTransactionApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsSavingAccountTransactionApplication.class, args);
	}

}
