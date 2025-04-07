package com.bank.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(scanBasePackages = "com.bank.payment")
public class BankPayApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankPayApplication.class, args);
	}

}
