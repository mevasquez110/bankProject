package com.nttdata.bank.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * DebitCardRequest is a data transfer object representing the request payload
 * for creating or updating a debit card. This class includes attributes such as
 * document number and primary account. It uses validation annotations to
 * enforce constraints and Lombok annotations for getters and setters.
 */

@Data
public class DebitCardRequest {

	@NotBlank(message = "Document number is mandatory")
	private String documentNumber;

	@NotBlank(message = "Primary account is mandatory")
	@Size(min = 14, max = 14, message = "Primary account must be exactly 14 digits")
	private String primaryAccount;
}
