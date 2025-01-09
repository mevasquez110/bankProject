package com.nttdata.bank.request;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import com.nttdata.bank.validation.PersonTypeValidator;
import javax.validation.constraints.Pattern;

/**
 * * CustomerRequest is a data transfer object that represents the request
 * payload for creating or updating a customer. * This class includes attributes
 * such as full name, company name, email, address, phone number, document type,
 * * document number, and person type. It uses validation annotations to enforce
 * constraints and Lombok annotations * for getters and setters.
 */

@Data
@PersonTypeValidator
public class CustomerRequest {

	private String fullName;
	private String companyName;

	@NotBlank(message = "Email is mandatory")
	@Email(message = "Invalid email format")
	private String email;

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

	@NotBlank(message = "Person type is mandatory")
	private String personType;
}
