package com.nttdata.bank.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * UpdateAccountRequest is a data transfer object representing the request
 * payload for updating an account. This class includes attributes such as
 * account number and amount. It uses validation annotations to enforce
 * constraints and Lombok annotations for getters and setters.
 */

@Data
public class UpdateAccountRequest {

	@NotNull(message = "Amount is mandatory")
	@Positive(message = "Amount must be greater than zero")
	private Double amount;

	@NotBlank(message = "Account number is mandatory")
	@Size(min = 14, max = 14, message = "Account number must be exactly 14 digits")
	private String accountNumber;

}
