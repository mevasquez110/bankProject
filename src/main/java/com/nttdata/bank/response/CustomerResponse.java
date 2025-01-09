package com.nttdata.bank.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * * CustomerResponse is a data transfer object that represents the response
 * payload for a customer. * This class includes attributes such as customer ID,
 * full name, company name, email, address, phone number, * document number, and
 * person type. It uses Jackson annotations for JSON inclusion and Lombok
 * annotations * for getters and setters.
 */

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerResponse {

	private String customeId;
	private String fullName;
	private String companyName;
	private String email;
	private String address;
	private String phoneNumber;
	private String documentNumber;
	private String personType;

}
