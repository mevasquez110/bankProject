package com.nttdata.bank.request;

import javax.validation.constraints.NotBlank;
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
	private String accountNumber;

}
