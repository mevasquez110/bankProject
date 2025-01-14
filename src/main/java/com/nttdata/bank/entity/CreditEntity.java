package com.nttdata.bank.entity;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

/**
 * * CreditEntity represents the credit document stored in the MongoDB
 * collection "credits". * This class includes various attributes related to the
 * credit, such as the credit amount, * interest rates, payment details, and
 * status details. It uses Lombok annotations for getters and setters, * and
 * Jackson for JSON inclusion.
 */

@Data
@Document(collection = "credits")
public class CreditEntity {

	@Id
	private String creditId;
	private String documentNumber;
	private Double amount;
	private String accountNumber;
	private Double annualInterestRate;
	private Integer numberOfInstallments;
	private Integer paymentDay;
	private Double annualLateInterestRate;
	private LocalDateTime createDate;
	private LocalDateTime updateDate;
	private LocalDateTime deleteDate;
	private Boolean isActive;

}
