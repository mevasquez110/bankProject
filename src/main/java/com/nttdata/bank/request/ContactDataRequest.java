package com.nttdata.bank.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Data;

/**
 * ContactDataRequest is a data transfer object that represents the request
 * payload for updating contact information. This class includes attributes such
 * as email, address, and phone number. It uses validation annotations to
 * enforce constraints and Lombok annotations for getters and setters.
 */

@Data
public class ContactDataRequest {

	@NotBlank(message = "Email is mandatory")
	@Email(message = "Invalid email format")
	private String email;

	@NotBlank(message = "Address is mandatory")
	private String address;

	@NotBlank(message = "Phone number is mandatory")
	@Size(max = 9, message = "Phone number must have a maximum of 9 digits")
	@Pattern(regexp = "\\d+", message = "Phone number must only contain digits")
	private String phoneNumber;

}
