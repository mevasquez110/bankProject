package com.nttdata.bank.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * CreditResponse is a data transfer object representing the response payload
 * for a credit. This class includes attributes such as credit ID, amount,
 * interest rates, number of installments, payment day, and late interest rate.
 * It uses Jackson annotations for JSON inclusion and Lombok annotations for
 * getters and setters.
 */

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreditResponse {

	private String creditId;
	private Double amount;
	private Double annualInterestRate;
	private Integer numberOfInstallments;
	private Integer paymentDay;
	private Double annualLateInterestRate;

}
