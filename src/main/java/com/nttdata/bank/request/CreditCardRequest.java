package com.nttdata.bank.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Data;

/**
 * * CreditCardRequest is a data transfer object that represents the request
 * payload for creating or updating a credit card. * This class includes
 * attributes such as customer ID, available credit, interest rates, and payment
 * day. * It uses validation annotations to enforce constraints and Lombok
 * annotations for getters and setters.
 */

@Data
public class CreditCardRequest {

    @NotBlank(message = "Document number is mandatory")
    private String documentNumber;

    @NotNull(message = "Available credit is mandatory")
    @Positive(message = "Available credit must be greater than zero")
    private Double availableCredit;

    @NotNull(message = "Annual interest rate is mandatory")
    @Min(value = 0, message = "Annual interest rate must be at least 0")
    @Max(value = 1, message = "Annual interest rate must be at most 1")
    private Double annualInterestRate;

    @NotNull(message = "Payment day is mandatory")
    @Positive(message = "Payment day must be a positive number")
    private Integer paymentDay;

    @NotNull(message = "Annual late interest rate is mandatory")
    @Min(value = 0, message = "Annual late interest rate must be at least 0")
    @Max(value = 1, message = "Annual late interest rate must be at most 1")
    private Double annualLateInterestRate;
}

