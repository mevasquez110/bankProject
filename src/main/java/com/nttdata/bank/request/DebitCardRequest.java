package com.nttdata.bank.request;

import javax.validation.constraints.NotBlank;
import com.nttdata.bank.validation.PersonTypeValidator;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@PersonTypeValidator
@EqualsAndHashCode(callSuper = false)
public class DebitCardRequest {

	@NotBlank(message = "Document number is mandatory")
	private String documentNumber;
}
