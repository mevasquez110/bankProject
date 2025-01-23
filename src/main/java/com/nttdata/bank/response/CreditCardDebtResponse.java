package com.nttdata.bank.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * CreditCardDebtResponse is a data transfer object representing the response
 * payload for a credit card debt inquiry. This class includes attributes such
 * as credit card number, total debt, share, and available credit. It uses
 * Jackson annotations for JSON inclusion and Lombok annotations for getters and
 * setters.
 */

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreditCardDebtResponse {

	private String creditCardNumber;
	private Double totalDebt;
	private Double share;
	private Double availableCredit;

}
