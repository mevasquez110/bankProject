package com.nttdata.bank.request;

import java.util.List;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import com.nttdata.bank.validation.AccountRequestValidator;
import lombok.Data;

/**
 * AccountRequest is a data transfer object that represents the request 
 * payload for creating or updating an account. This class includes attributes 
 * such as holder documents, authorized signatory documents, account type, 
 * opening amount, and currency. It uses validation annotations to enforce 
 * constraints and Lombok annotations for getters and setters.
 */

@Data
@AccountRequestValidator
public class AccountRequest {

    private List<String> holderDoc;
    
    private List<String> authorizedSignatoryDoc;

    @NotBlank(message = "Account type is mandatory")
    private String accountType;

    @NotNull(message = "Opening amount is mandatory")
    @Min(value = 0, message = "Opening amount must be zero or greater")
    private Double openingAmount;

}
