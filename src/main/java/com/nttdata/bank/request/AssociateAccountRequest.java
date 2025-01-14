package com.nttdata.bank.request;

import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * AssociateAccountRequest is a data transfer object that represents the request 
 * payload for associating accounts with a primary account. This class includes 
 * attributes such as a list of associated accounts and the primary account.
 * It uses Lombok annotations for getters and setters.
 */

@Data
public class AssociateAccountRequest {
	
    private List<String> associatedAccounts;
    
    @NotBlank(message = "Primary account is mandatory")
    private String primaryAccount;
}
