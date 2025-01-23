package com.nttdata.bank.entity;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

/**
 * TransactionEntity represents the transaction document stored in the MongoDB
 * collection "transactions". This class includes attributes related to the
 * transaction, such as operation number, account details, credit card details,
 * amount, commission, transaction type, and status. It uses Lombok annotations
 * for getters and setters.
 */

@Data
@Document(collection = "transactions")
public class TransactionEntity {

	@Id
	private String id;
	private String operationNumber;
	private String accountNumberWithdraws;
	private String accountNumberReceive;
	private String nameWithdraws;
	private String nameReceive;
	private String creditCardNumber;
	private String creditId;
	private Double amount;
	private Double commission;
	private String transactionType;
	private Integer numberOfInstallments;
	private LocalDateTime createDate;
	private LocalDateTime updateDate;
	private LocalDateTime deleteDate;
	private Boolean isActive;
}
