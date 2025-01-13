package com.nttdata.bank.response;

import com.nttdata.bank.validation.PersonTypeValidator;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@PersonTypeValidator
@EqualsAndHashCode(callSuper = false)
public class DebitCardResponse {

	private String debitCardNumber;
	private Boolean isBlocked;
	private Boolean isActive;
}
