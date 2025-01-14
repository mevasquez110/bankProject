package com.nttdata.bank.entity;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * * AccountEntity represents the account document stored in the MongoDB
 * collection "accounts". * This class includes various attributes related to
 * the bank account, such as account number, * transaction limits, commission,
 * balance, associated customers, and status details. * It uses Lombok
 * annotations for getters and setters, and Jackson for JSON inclusion.
 */

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "accounts")
public class AccountEntity {

	@Id
	private String id;
	private String accountNumber;
	private Integer monthlyTransactionLimit;
	private Double maintenanceCommission;
	private LocalDateTime withdrawalDepositDate;
	private String currency;
	private Double amount;
	private Double commissionPending;
	private List<String> holderDoc;
	private List<String> authorizedSignatoryDoc;
	private String accountType;
	private Boolean allowWithdrawals;
	private LocalDateTime createDate;
	private LocalDateTime updateDate;
	private LocalDateTime deleteDate;
	private Boolean isBlocked;
	private Boolean isActive;
}
