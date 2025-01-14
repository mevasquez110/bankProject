package com.nttdata.bank.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Data;

/**
 * MobileTransferRequest is a data transfer object that represents the request 
 * payload for making a mobile transfer. This class includes attributes such as 
 * mobile number of the sender, mobile number of the recipient, and amount. 
 * It uses validation annotations to enforce constraints and Lombok annotations 
 * for getters and setters.
 */

@Data
public class MobileTransferRequest {

    @NotBlank(message = "Mobile number sender is mandatory")
    private String mobileNumberSender;

    @NotBlank(message = "Mobile number recipient is mandatory")
    private String mobileNumberRecipient;

    @NotNull(message = "Amount is mandatory")
    @Positive(message = "Amount must be greater than zero")
    private Double amount;
}
