package com.nttdata.bank.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import com.nttdata.bank.validation.PersonTypeValidator;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@PersonTypeValidator
@EqualsAndHashCode(callSuper = false)
public class YankiUpdateRequest {
	
	@NotBlank(message = "Phone number is mandatory")
	@Size(max = 9, message = "Phone number must have a maximum of 9 digits")
	@Pattern(regexp = "\\d+", message = "Phone number must only contain digits")
	private String phoneNumber;
	
	@NotBlank(message = "Account number is mandatory")
	private String accountNumber;
}
