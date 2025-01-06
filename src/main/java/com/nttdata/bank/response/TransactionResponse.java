package com.nttdata.bank.response;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionResponse {

    private Integer transactionId;
	private String accountNumber;
	private String creditCardNumber;
    private Integer creditId;
	private Double amount;
	private LocalDateTime createDate;
	private String transactionType;
}
