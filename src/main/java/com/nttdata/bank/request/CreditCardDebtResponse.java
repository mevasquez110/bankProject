package com.nttdata.bank.request;

import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreditCardDebtResponse {

	@NotNull(message = "CreditCardNumber is mandatory")
    private String creditCardNumber;
	
	@NotNull(message = "TotalDebt is mandatory")
	private Double totalDebt;
	
	@NotNull(message = "Share is mandatory")
	private Double share;

}
