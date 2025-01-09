package com.nttdata.bank.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "credit_cards")
public class CreditCardEntity {

	@Id
	private String creditCardId;
	private String customerId;
	private Double availableCredit;
	private Double annualInterestRate;
	private Double annualLateInterestRate;
	private Integer paymentDay;
	private LocalDate createDate;
	private LocalDateTime updateDate;
	private LocalDateTime deleteDate;
	private Boolean isActive;
}
