package com.nttdata.bank.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreditCardResponse {
	
	private Integer creditId;
	private Double amount;
	private Double interestRate;
	private Integer paymentDay;
	private LocalDateTime createDate;

}
