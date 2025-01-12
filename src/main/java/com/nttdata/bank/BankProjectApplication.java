package com.nttdata.bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class BankProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankProjectApplication.class, args);
	}

}
