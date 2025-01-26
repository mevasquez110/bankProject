package com.nttdata.bank.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import com.nttdata.bank.validation.DepositRequestValidator;
import lombok.Data;

/**
 * DepositRequest is a data transfer object representing the request payload for
 * making a deposit. This class includes attributes such as debit card number,
 * account number, document number, and amount. It uses validation annotations
 * to enforce constraints and Lombok annotations for getters and setters.
 */

@Data
@DepositRequestValidator
public class DepositRequest {

	private String debitCardNumber;

	private String accountNumber;

	@NotBlank(message = "Document number is mandatory")
	private String documentNumber;

	@NotNull(message = "Amount is mandatory")
	@Positive(message = "Amount must be greater than zero")
	private Double amount;
}
