package com.nttdata.bank.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import lombok.Data;

/**
 * PayCreditCardRequest is a data transfer object representing the request
 * payload for paying a credit card. This class includes attributes such as
 * credit card number and amount. It uses validation annotations to enforce
 * constraints and Lombok annotations for getters and setters.
 */

@Data
public class PayCreditCardRequest {

	@Size(min = 16, max = 16, message = "Credit card number must be exactly 16 digits")
	private String creditCardNumber;

	@Size(min = 14, max = 14, message = "Account number must be exactly 14 digits")
	private String accountNumber;

	private String documentNumber;

	@NotNull(message = "Amount is mandatory")
	@Positive(message = "Amount must be greater than zero")
	private Double amount;

}
