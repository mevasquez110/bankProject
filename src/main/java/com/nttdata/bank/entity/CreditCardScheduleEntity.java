package com.nttdata.bank.entity;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

/**
 * CreditCardScheduleEntity represents the credit card schedule document stored
 * in the MongoDB collection "credit_schedules". This class includes attributes
 * related to the credit card schedule, such as payment dates, interest amounts,
 * principal amounts, current debt, and the list of consumptions associated with
 * each quota. It uses Lombok annotations for getters and setters.
 */

@Data
@Document(collection = "credit_schedules")
public class CreditCardScheduleEntity {

	@Id
	private String id;
	private String creditCardNumber;
	private LocalDate paymentDate;
	private Double interestAmount;
	private Double lateAmount;
	private Double principalAmount;
	private Double currentDebt;
	private Boolean paid;
	private List<Consumption> consumptionQuota;

}
