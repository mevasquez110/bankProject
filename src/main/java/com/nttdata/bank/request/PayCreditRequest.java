package com.nttdata.bank.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * PayCreditRequest is a data transfer object representing the request payload
 * for making a credit payment. This class includes attributes such as credit
 * ID, amount, document number, and account number. It uses validation
 * annotations to enforce constraints and Lombok annotations for getters and
 * setters.
 */

@Data
public class PayCreditRequest {

	@NotBlank(message = "Credit ID is mandatory")
	private String creditId;

	@NotNull(message = "Amount is mandatory")
	@Positive(message = "Amount must be greater than zero")
	private Double amount;

	@NotBlank(message = "Document number is mandatory")
	private String documentNumber;

	@NotBlank(message = "Account number is mandatory")
	@Size(min = 14, max = 14, message = "Account number must be exactly 14 digits")
	private String accountNumber;
}
