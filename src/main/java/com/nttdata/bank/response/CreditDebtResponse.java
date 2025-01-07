package com.nttdata.bank.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreditDebtResponse {

    private String creditId;
	private Double totalDebt;
	private Double share;

}
