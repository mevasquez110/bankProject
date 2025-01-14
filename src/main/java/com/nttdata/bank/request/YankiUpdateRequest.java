package com.nttdata.bank.request;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class YankiUpdateRequest {

	@NotBlank(message = "Account number is mandatory")
	private String accountNumber;

}
