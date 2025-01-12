package com.nttdata.bank.response;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * * AccountResponse is a data transfer object that represents the response
 * payload for an account. * This class includes attributes such as account
 * number, monthly transaction limit, maintenance commission, * withdrawal
 * deposit date, and creation date. It uses Jackson annotations for JSON
 * inclusion and * Lombok annotations for getters and setters.
 */

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountResponse {

	private String accountNumber;
	private Integer monthlyTransactionLimit;
	private Double maintenanceCommission;
	private LocalDateTime withdrawalDepositDate;
	private Double amount;
	private Boolean isBlocked;
	private Boolean allowWithdrawals;


}
