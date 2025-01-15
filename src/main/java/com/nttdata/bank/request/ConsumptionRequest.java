package com.nttdata.bank.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Data;

/**
 * ConsumptionRequest is a data transfer object that represents the request
 * payload for recording a consumption made using a credit card. This class
 * includes attributes such as credit card number, amount, share, and
 * productOrServiceName. It uses validation annotations to enforce constraints
 * and Lombok annotations for getters and setters.
 */

@Data
public class ConsumptionRequest {

	@NotBlank(message = "Credit card number is mandatory")
	private String creditCardNumber;

	@NotNull(message = "Amount is mandatory")
	@Positive(message = "Amount must be greater than zero")
	private Double amount;

	@NotBlank(message = "Share is mandatory")
	private Integer share;

	@NotBlank(message = "Product or service name is mandatory")
	private String productOrServiceName;
}
