package com.nttdata.bank.response;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionResponse {

    private String transactionId;
	private String accountNumber;
	private String creditCardNumber;
    private String creditId;
	private Double amount;
	private LocalDateTime createDate;
	private String transactionType;
}
