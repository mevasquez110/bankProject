package com.nttdata.bank.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Data;

@Data
public class ConsumptionRequest {

	@NotBlank(message = "Credit card number is mandatory")
	private String creditCardNumber;

	@NotNull(message = "Amount is mandatory")
	@Positive(message = "Amount must be greater than zero")
	private Double amount;
	
	@NotBlank(message = "Share is mandatory")
	private Integer share;
	
	
}
