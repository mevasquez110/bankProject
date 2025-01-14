package com.nttdata.bank.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Data;

/**
 * PayCreditCardRequest is a data transfer object that represents the request
 * payload for paying a credit card. This class includes attributes such as
 * credit card number and amount. It uses validation annotations to enforce
 * constraints and Lombok annotations for getters and setters.
 */

@Data
public class PayCreditCardRequest {

	@NotBlank(message = "Credit card number is mandatory")
	private String creditCardNumber;

	@NotNull(message = "Amount is mandatory")
	@Positive(message = "Amount must be greater than zero")
	private Double amount;

}
