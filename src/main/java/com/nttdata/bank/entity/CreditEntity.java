package com.nttdata.bank.entity;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "credits")
public class CreditEntity {
	
	@Id
	private String creditId;
	private String customerId;
	private Double amount;
	private String accountNumber;
	private Double annualInterestRate;
	private Integer numberOfInstallments;
	private Integer paymentDay;
	private LocalDateTime createDate;
	private Double annualLateInterestRate;
	private String status;
	
}
