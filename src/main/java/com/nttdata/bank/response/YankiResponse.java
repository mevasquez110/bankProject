package com.nttdata.bank.response;

import com.nttdata.bank.validation.PersonTypeValidator;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@PersonTypeValidator
@EqualsAndHashCode(callSuper = false)
public class YankiResponse {

	private String name;
	private String phoneNumber;
}
