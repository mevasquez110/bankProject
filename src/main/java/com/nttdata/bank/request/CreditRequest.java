package com.nttdata.bank.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreditRequest {

	@NotBlank(message = "CustomerId is mandatory")
	private String customerId;

	@NotNull(message = "Amount is mandatory")
	@Positive(message = "Amount must be greater than zero")
	private Double amount;

	@NotBlank(message = "AccountNumber is mandatory")
	private String accountNumber;

	@NotNull(message = "AnnualInterestRate is mandatory")
	@Positive(message = "AnnualInterestRate must be greater than zero")
	private Double annualInterestRate;

	@NotNull(message = "NumberOfInstallments is mandatory")
	@Positive(message = "NumberOfInstallments must be greater than zero")
	private Integer numberOfInstallments;

	@NotNull(message = "PaymentDay is mandatory")
	@Positive(message = "PaymentDay must be a positive number")
	private Integer paymentDay;

	@NotNull(message = "AnnualLateInterestRate is mandatory")
	@Positive(message = "AnnualLateInterestRate must be greater than zero")
	private Double annualLateInterestRate;
}
