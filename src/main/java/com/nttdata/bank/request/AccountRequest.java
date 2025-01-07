package com.nttdata.bank.request;

import java.util.List;
import com.nttdata.bank.bean.AuthorizedSignatory;
import com.nttdata.bank.validation.AccountRequestValidator;

import lombok.Data;

@Data
@AccountRequestValidator
public class AccountRequest {

	private List<String> customerId;
	private List<AuthorizedSignatory> authorizedSignatory;
	private String accountType;
}
