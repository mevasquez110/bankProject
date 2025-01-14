package com.nttdata.bank.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Data;

/**
 * AccountTransferRequest is a data transfer object that represents the request
 * payload for transferring funds between accounts. This class includes
 * attributes such as sender's account number, recipient's account number, and
 * the amount to be transferred. It uses validation annotations to enforce
 * constraints and Lombok annotations for getters and setters.
 */

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
