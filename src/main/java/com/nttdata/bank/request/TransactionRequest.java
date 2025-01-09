package com.nttdata.bank.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Data;

@Data
public class TransactionRequest {
	
    @NotBlank(message = "Account number is mandatory")
    private String accountNumber;

    @NotBlank(message = "Credit card number is mandatory")
    private String creditCardNumber;

    @NotBlank(message = "Credit id is mandatory")
    private String creditId;

    @NotNull(message = "Amount is mandatory")
    @Positive(message = "Amount must be greater than zero")
    private Double amount;
}
