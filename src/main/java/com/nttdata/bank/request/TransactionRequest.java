package com.nttdata.bank.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Data;

/**
 * * TransactionRequest is a data transfer object that represents the request
 * payload for creating a transaction. * This class includes attributes such as
 * account number, credit card number, credit ID, and transaction amount. * It
 * uses validation annotations to enforce constraints and Lombok annotations for
 * getters and setters.
 */

@Data
public class TransactionRequest {

	@NotBlank(message = "Account number is mandatory")
	private String accountNumber;

	@NotBlank(message = "Credit card number is mandatory")
	private String creditCardNumber;

	@NotBlank(message = "Credit id is mandatory")
	private String creditId;

	@NotNull(message = "Amount is mandatory")
	@Positive(message = "Amount must be greater than zero")
	private Double amount;
}
