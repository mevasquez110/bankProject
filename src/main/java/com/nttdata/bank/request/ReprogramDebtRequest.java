package com.nttdata.bank.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Data;

@Data
public class ReprogramDebtRequest {

	@NotBlank(message = "Credit id is mandatory")
	private String creditId;

	@NotNull(message = "New installments is mandatory")
	@Positive(message = "New installments must be greater than zero")
	private Integer newInstallments;

	@NotNull(message = "New interest rate is mandatory")
	@Positive(message = "New interest rate must be greater than zero")
	private Double newInterestRate;

	@NotNull(message = "New late interest rate is mandatory")
	@Positive(message = "New late interest rate must be greater than zero")
	private Double newLateInterestRate;
	
	@NotNull(message = "New number of installments is mandatory")
	@Positive(message = "New number of installments must be greater than zero")
	private Integer newNumberOfInstallments;


	@NotNull(message = "New payment day is mandatory")
	@Positive(message = "New payment day must be a positive number")
	private Integer newPaymentDay;
}
