package com.nttdata.bank.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

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
