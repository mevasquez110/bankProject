package com.nttdata.bank.response;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * * CreditCardResponse is a data transfer object that represents the response
 * payload for a credit card. * This class includes attributes such as credit
 * card number, available credit, interest rates, payment day, * and creation
 * date. It uses Jackson annotations for JSON inclusion and Lombok annotations
 * for getters and setters.
 */

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreditCardResponse {

	private String creditCardNumber;
	private Double availableCredit;
	private Double annualInterestRate;
	private Double annualLateInterestRate;
	private Integer paymentDay;
	private LocalDateTime createDate;

}
