package com.nttdata.bank.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DebtResponse {

	private String creditNumber;
    private Integer creditId;
	private Double totalDebt;
	private Double share;

}
