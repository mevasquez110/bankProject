package com.nttdata.bank.request;

import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DebitCardRequest is a data transfer object that represents the request 
 * payload for creating or updating a debit card. This class includes the 
 * attribute document number. It uses validation annotations to enforce 
 * constraints and Lombok annotations for getters and setters.
 */

@Data
public class DebitCardRequest {

    @NotBlank(message = "Document number is mandatory")
    private String documentNumber;

}
