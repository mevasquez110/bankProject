package com.nttdata.bank.request;

import lombok.Data;

@Data
public class AuthorizedSignatoryRequest {

	private String name;
	private String address;
	private String phoneNumber;
	private String documentNumber;
}
