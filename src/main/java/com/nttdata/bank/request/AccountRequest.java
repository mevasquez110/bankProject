package com.nttdata.bank.request;

import java.util.List;

import com.nttdata.bank.validation.AccountRequestValidator;

import lombok.Data;

@Data
@AccountRequestValidator
public class AccountRequest {

	private List<String> customerId;
	private List<AuthorizedSignatoryRequest> authorizedSignatory;
	private String accountType;
}
