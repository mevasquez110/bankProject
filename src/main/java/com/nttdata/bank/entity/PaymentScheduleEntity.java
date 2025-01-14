package com.nttdata.bank.entity;

import java.time.LocalDate;
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
@Document(collection = "payment_schedules")
public class PaymentScheduleEntity {

	@Id
	private String id;
	private LocalDate paymentDate;
	private Double debtAmount;
	private Double sharePayment;
	private String creditId;
	private String creditCardNumber;
	private Boolean paid;
}
