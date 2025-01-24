package com.nttdata.bank.entity;

import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

/**
 * CreditScheduleEntity represents the credit schedule document stored in the
 * MongoDB collection "credit_schedules". This class includes attributes related
 * to the credit schedule, such as payment date, interest amount, late amount,
 * principal amount, current debt, total debt, payment status, and credit ID. It
 * uses Lombok annotations for getters and setters.
 */

@Data
@Document(collection = "credit_schedules")
public class CreditScheduleEntity {

	@Id
	private String id;
	private String creditId;
	private LocalDate paymentDate;
	private Double interestAmount;
	private Double lateAmount;
	private Double principalAmount;
	private Double currentDebt;
	private Double balance;
	private Boolean paid;
}
