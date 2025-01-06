package com.nttdata.bank.bean;

import lombok.Data;

@Data
public class AuthorizedSignatory {

	private String name;
	private String address;
	private String phoneNumber;
	private String documentNumber;
}
