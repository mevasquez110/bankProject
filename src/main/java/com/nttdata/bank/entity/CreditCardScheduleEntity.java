package com.nttdata.bank.entity;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

/**
 * * CustomerEntity represents the customer document stored in the MongoDB
 * collection "customers". * This class includes various attributes related to
 * the customer, such as personal and contact information, * document details,
 * and status details. It uses Lombok annotations for getters and setters, and
 * Jackson for JSON inclusion.
 */

@Data
@Document(collection = "credit_schedules")
public class CreditCardScheduleEntity {

	@Id
	private String id;
	private String creditCartNumber;
	private LocalDate paymentDate;
	private Double interestAmount;
	private Double lateAmount;
	private Double principalAmount;
	private Double currentDebt;
	private Double totalDebt;
	private Boolean paid;
	private List<String> consumptionQuota;
;
}
