package com.nttdata.bank.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreditCardDebtResponse {

    private String creditCardNumber;
	private Double totalDebt;
	private Double share;

}
