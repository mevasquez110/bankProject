package com.nttdata.bank.entity;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "transactions")
public class TransactionEntity {

	@Id
	private String id;
	private String accountNumber;
	private String creditCardNumber;
	private String creditId;
	private Double amount;
	private LocalDateTime createDate;
	private String transactionType;
	private Integer numberOfInstallments;
	private LocalDateTime transactionDate;
}
