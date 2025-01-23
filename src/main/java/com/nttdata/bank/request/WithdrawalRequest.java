package com.nttdata.bank.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Data;

/**
 * WithdrawalRequest is a data transfer object representing the request payload
 * for making a withdrawal from a debit card. This class includes attributes
 * such as debit card number, document number, and amount. It uses validation
 * annotations to enforce constraints and Lombok annotations for getters and
 * setters.
 */

@Data
public class WithdrawalRequest {

	@NotBlank(message = "Debit card number is mandatory")
	private String debitCardNumber;

	@NotBlank(message = "Document number is mandatory")
	private String documentNumber;

	@NotNull(message = "Amount is mandatory")
	@Positive(message = "Amount must be greater than zero")
	private Double amount;
}
