package com.nttdata.bank.entity;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

/**
 * CreditCardEntity represents the credit card document stored in the MongoDB
 * collection "credit_cards". This class includes attributes related to the
 * credit card, such as available credit, interest rates, payment day, and
 * status. It uses Lombok annotations for getters and setters.
 */

@Data
@Document(collection = "credit_cards")
public class CreditCardEntity {

	@Id
	private String id;
	private String creditCardNumber;
	private String documentNumber;
	private Double availableCredit;
	private Double annualInterestRate;
	private Double annualLateInterestRate;
	private Integer paymentDay;
	private Boolean allowConsumption;
	private LocalDateTime createDate;
	private LocalDateTime updateDate;
	private LocalDateTime deleteDate;
	private Boolean isActive;
}
