package com.nttdata.bank.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import com.nttdata.bank.validation.YankiValidator;
import lombok.Data;

/**
 * YankiRequest is a data transfer object representing the request payload for
 * creating or updating a Yanki entity. This class includes attributes such as
 * name, phone number, document type, and document number. It uses validation
 * annotations to enforce constraints and Lombok annotations for getters and
 * setters.
 */

@Data
@YankiValidator
public class YankiRequest {

	@NotBlank(message = "Name is mandatory")
	private String name;

	@NotBlank(message = "Phone number is mandatory")
	@Size(max = 9, message = "Phone number must have a maximum of 9 digits")
	@Pattern(regexp = "\\d+", message = "Phone number must only contain digits")
	private String phoneNumber;

	@NotBlank(message = "Document type is mandatory")
	private String documentType;

	@NotBlank(message = "Document number is mandatory")
	private String documentNumber;
}
