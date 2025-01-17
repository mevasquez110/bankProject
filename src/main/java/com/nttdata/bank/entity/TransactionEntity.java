package com.nttdata.bank.entity;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

/**
 * * PaymentScheduleEntity represents the payment schedule document stored in
 * the MongoDB collection "payment_schedules". * This class includes various
 * attributes related to the payment schedule, such as payment date, debt
 * amount, * share payment, credit ID, credit card number, and payment status.
 * It uses Lombok annotations for getters and setters, * and Jackson for JSON
 * inclusion.
 */

@Data
@Document(collection = "transactions")
public class TransactionEntity {

	@Id
	private String id;
	private String operationNumber;
	private String accountNumber;
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
