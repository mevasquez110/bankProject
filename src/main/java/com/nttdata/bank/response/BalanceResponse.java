package com.nttdata.bank.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * BalanceResponse is a data transfer object representing the response payload
 * for a balance inquiry. This class includes attributes such as account number,
 * amount, and whether withdrawals are allowed. It uses Jackson annotations for
 * JSON inclusion and Lombok annotations for getters and setters.
 */

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BalanceResponse {

	private String accountNumber;
	private Double amount;
	private Boolean allowWithdrawals;

}
