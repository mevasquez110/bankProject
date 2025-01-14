package com.nttdata.bank.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Data;

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
