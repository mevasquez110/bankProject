package com.nttdata.bank.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * CreditDebtResponse is a data transfer object representing the response
 * payload for a credit debt inquiry. This class includes attributes such as
 * credit ID, available amount, total debt, and share. It uses Jackson
 * annotations for JSON inclusion and Lombok annotations for getters and
 * setters.
 */

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreditDebtResponse {

	private String creditId;
	private Double availableAmount;
	private Double totalDebt;
	private Double share;

}
