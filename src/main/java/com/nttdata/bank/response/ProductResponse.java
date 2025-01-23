package com.nttdata.bank.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * ProductResponse is a data transfer object representing the response payload
 * for a product. This class includes attributes such as credit ID, credit card
 * number, account number, account type, and product type. It uses Jackson
 * annotations for JSON inclusion and Lombok annotations for getters and
 * setters.
 */

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponse {

	private String creditId;
	private String creditCardNumber;
	private String accountNumber;
	private String accountType;
	private String productType;
}
