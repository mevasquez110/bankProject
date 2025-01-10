package com.nttdata.bank.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import com.nttdata.bank.validation.PersonTypeValidator;

/**
 * * CustomerRequest is a data transfer object that represents the request
 * payload for creating or updating a customer. * This class includes attributes
 * such as full name, company name, email, address, phone number, document type,
 * * document number, and person type. It uses validation annotations to enforce
 * constraints and Lombok annotations * for getters and setters.
 */

@Data
@PersonTypeValidator
@EqualsAndHashCode(callSuper = false)
public class CustomerRequest extends ContactDataRequest {

	private String fullName;
	private String companyName;

	@NotBlank(message = "Document type is mandatory")
	private String documentType;

	@NotBlank(message = "Document number is mandatory")
	private String documentNumber;

	@NotBlank(message = "Person type is mandatory")
	private String personType;
}
