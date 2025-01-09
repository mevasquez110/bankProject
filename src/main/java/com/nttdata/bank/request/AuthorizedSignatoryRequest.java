package com.nttdata.bank.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthorizedSignatoryRequest {

	@NotBlank(message = "Name is mandatory")
	private String name;
	
	@NotBlank(message = "Address is mandatory")
	private String address;
	
	@NotBlank(message = "Phone number is mandatory")
	@Size(max = 9, message = "Phone number must have a maximum of 9 digits")
	@Pattern(regexp = "\\d+", message = "Phone number must only contain digits")
	private String phoneNumber;
	
	@NotBlank(message = "Document type is mandatory")
	private String documentType;
	
	@NotBlank(message = "Document number is mandatory")
	private String documentNumber;
}
