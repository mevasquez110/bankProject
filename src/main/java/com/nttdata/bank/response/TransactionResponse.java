package com.nttdata.bank.response;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * TransactionResponse is a data transfer object representing the response
 * payload for a transaction. This class includes attributes such as operation
 * number, account numbers, names, credit card number, credit ID, transaction
 * amount, commission, transaction date, and transaction type. It uses Jackson
 * annotations for JSON inclusion and Lombok annotations for getters and
 * setters.
 */

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionResponse {

	private String operationNumber;
	private String accountNumberWithdraws;
	private String nameWithdraws;
	private String accountNumberReceive;
	private String nameReceive;
	private String creditCardNumber;
	private String creditId;
	private Double amount;
	private Double commission;
	private LocalDateTime transactionDate;
	private String transactionType;
}
