package com.nttdata.bank.response;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)

public class DebitCardResponse {

	private String debitCardNumber;
	private List<String> associatedAccounts;
	private String primaryAccount;
	private Boolean isBlocked;
	private Boolean isActive;
}
