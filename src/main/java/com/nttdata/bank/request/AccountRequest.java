package com.nttdata.bank.request;

import java.util.List;
import javax.validation.constraints.NotBlank;
import com.nttdata.bank.validation.AccountRequestValidator;
import lombok.Data;

@Data
@AccountRequestValidator
public class AccountRequest {

    private List<String> customerId;
    
    private List<AuthorizedSignatoryRequest> authorizedSignatory;
    
    @NotBlank(message = "Account type is mandatory")
    private String accountType;
    
}
