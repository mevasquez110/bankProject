package com.nttdata.bank.request;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DebitCardRequest {

	@NotBlank(message = "Document number is mandatory")
	private String documentNumber;
}
