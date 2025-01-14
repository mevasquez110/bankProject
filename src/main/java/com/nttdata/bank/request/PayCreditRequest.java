package com.nttdata.bank.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Data;

/**
 * PayCreditRequest is a data transfer object that represents the request 
 * payload for making a credit payment. This class includes attributes such as 
 * credit ID and amount. It uses validation annotations to enforce constraints 
 * and Lombok annotations for getters and setters.
 */

@Data
public class PayCreditRequest {

    @NotBlank(message = "Credit id is mandatory")
    private String creditId;

    @NotNull(message = "Amount is mandatory")
    @Positive(message = "Amount must be greater than zero")
    private Double amount;
}
