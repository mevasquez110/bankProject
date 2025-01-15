package com.nttdata.bank.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Data;

/**
 * DepositRequest is a data transfer object that represents the request 
 * payload for making a deposit. This class includes attributes such as 
 * credit card number and amount. It uses validation annotations to enforce 
 * constraints and Lombok annotations for getters and setters.
 */

@Data
public class DepositRequest {

    @NotBlank(message = "Credit card number is mandatory")
    private String creditCardNumber;
    
	@NotBlank(message = "Account number is mandatory")
	private String accountNumber;

    @NotNull(message = "Amount is mandatory")
    @Positive(message = "Amount must be greater than zero")
    private Double amount;
}
