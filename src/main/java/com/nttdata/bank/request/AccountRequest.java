package com.nttdata.bank.request;

import java.util.List;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import com.nttdata.bank.validation.AccountRequestValidator;
import lombok.Data;

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

	@NotBlank(message = "Currency is mandatory")
	private String currency;

}
