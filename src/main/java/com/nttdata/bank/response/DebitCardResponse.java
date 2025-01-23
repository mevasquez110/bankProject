package com.nttdata.bank.response;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * DebitCardResponse is a data transfer object representing the response payload
 * for a debit card. This class includes attributes such as debit card number,
 * associated accounts, primary account, and status details like whether the
 * card is blocked or active. It uses Lombok annotations for getters and
 * setters, and Jackson annotations for JSON inclusion.
 */

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DebitCardResponse {

	private String debitCardNumber;
	private List<String> associatedAccounts;
	private String primaryAccount;
	private Boolean isBlocked;
	private Boolean isActive;
}
