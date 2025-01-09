package com.nttdata.bank.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreditCardRequest {

	@NotBlank(message = "Customer id is mandatory")
	private String customerId;

	@NotNull(message = "Available credit is mandatory")
	@Positive(message = "Available credit must be greater than zero")
	private Double availableCredit;

	@NotNull(message = "Annual interest rate is mandatory")
	@Positive(message = "Annual interest rate must be greater than zero")
	private Double annualInterestRate;

	@NotNull(message = "Payment day is mandatory")
	@Positive(message = "Payment day must be a positive number")
	private Integer paymentDay;

	@NotNull(message = "Annual late interest rate is mandatory")
	@Positive(message = "Annual late interest rate must be greater than zero")
	private Double annualLateInterestRate;
	
}
