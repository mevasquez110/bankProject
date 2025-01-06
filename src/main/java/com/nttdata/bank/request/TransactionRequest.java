package com.nttdata.bank.request;

import lombok.Data;

@Data
public class TransactionRequest {

	private String accountNumber;
	private String creditCardNumber;
    private Integer creditId;
	private Double amount;
}
