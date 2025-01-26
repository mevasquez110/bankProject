package com.nttdata.bank.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * YankiUpdateRequest is a data transfer object representing the request payload
 * for updating a Yanki entity. This class includes the account number
 * attribute. It uses validation annotations to enforce constraints and Lombok
 * annotations for getters and setters.
 */

@Data
public class YankiUpdateRequest {

	@NotBlank(message = "Account number is mandatory")
	@Size(min = 14, max = 14, message = "Acount number must be exactly 14 digits")
	private String accountNumber;

}
