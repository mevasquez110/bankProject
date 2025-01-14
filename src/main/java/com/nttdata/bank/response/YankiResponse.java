package com.nttdata.bank.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class YankiResponse {

	private String name;
	private String phoneNumber;
}
