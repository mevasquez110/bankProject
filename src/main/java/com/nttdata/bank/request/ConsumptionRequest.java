package com.nttdata.bank.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import lombok.Data;

/**
 * ConsumptionRequest is a data transfer object that represents the request
 * payload for recording a consumption made using a credit card. This class
 * includes attributes such as credit card number, amount, number of
 * installments, and product or service name. It uses validation annotations to
 * enforce constraints and Lombok annotations for getters and setters.
 */

@Data
public class ConsumptionRequest {

	@NotBlank(message = "Credit card number is mandatory")
	@Size(min = 16, max = 16, message = "Credit card number must be exactly 16 digits")
	private String creditCardNumber;

	@NotNull(message = "Amount is mandatory")
	@Positive(message = "Amount must be greater than zero")
	private Double amount;

	@NotNull(message = "Number of installments is mandatory")
	@Positive(message = "Number of installments must be greater than zero")
	private Integer numberOfInstallments;

	@NotBlank(message = "Product or service name is mandatory")
	private String productOrServiceName;
}
