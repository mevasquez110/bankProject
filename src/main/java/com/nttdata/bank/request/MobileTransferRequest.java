package com.nttdata.bank.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import lombok.Data;

/**
 * MobileTransferRequest is a data transfer object representing the request
 * payload for making a mobile transfer. This class includes attributes such as
 * mobile number of the sender, mobile number of the recipient, and amount. It
 * uses validation annotations to enforce constraints and Lombok annotations for
 * getters and setters.
 */

@Data
public class MobileTransferRequest {

	@NotBlank(message = "Mobile number withdraws is mandatory")
	@Size(max = 9, message = "Mobile number withdraws must have a maximum of 9 digits")
	@Pattern(regexp = "\\d+", message = "Mobile number must only contain digits")
	private String mobileNumberWithdraws;

	@NotBlank(message = "Mobile number receive is mandatory")
	@Size(max = 9, message = "Mobile number receive must have a maximum of 9 digits")
	@Pattern(regexp = "\\d+", message = "Mobile number must only contain digits")
	private String mobileNumberReceive;

	@NotBlank(message = "Document number withdraws is mandatory")
	private String documentNumberWithdraws;

	@NotBlank(message = "Document number receive is mandatory")
	private String documentNumberReceive;

	@NotNull(message = "Amount is mandatory")
	@Positive(message = "Amount must be greater than zero")
	private Double amount;
}
