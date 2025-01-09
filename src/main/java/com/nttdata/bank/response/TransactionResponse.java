package com.nttdata.bank.response;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * * TransactionResponse is a data transfer object that represents the response
 * payload for a transaction. * This class includes attributes such as
 * transaction ID, account number, credit card number, credit ID, * transaction
 * amount, creation date, and transaction type. It uses Jackson annotations for
 * JSON inclusion * and Lombok annotations for getters and setters.
 */

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionResponse {

	private String transactionId;
	private String accountNumber;
	private String creditCardNumber;
	private String creditId;
	private Double amount;
	private LocalDateTime createDate;
	private String transactionType;
}
