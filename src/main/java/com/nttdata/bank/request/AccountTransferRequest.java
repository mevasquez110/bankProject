package com.nttdata.bank.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Data;

@Data
public class AccountTransferRequest {

	@NotBlank(message = "Account number sender is mandatory")
	private String accountNumberSender;
	
	@NotBlank(message = "Account number recipient is mandatory")
	private String accountNumberRecipient;

	@NotNull(message = "Amount is mandatory")
	@Positive(message = "Amount must be greater than zero")
	private Double amount;
}
