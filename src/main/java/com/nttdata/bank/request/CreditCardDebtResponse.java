package com.nttdata.bank.request;

import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreditCardDebtResponse {

	@NotNull(message = "Credit card number is mandatory")
    private String creditCardNumber;
	
	@NotNull(message = "Total debt is mandatory")
	private Double totalDebt;
	
	@NotNull(message = "Share is mandatory")
	private Double share;

}
