package com.nttdata.bank.entity;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.nttdata.bank.request.AuthorizedSignatoryRequest;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "accounts")
public class AccountEntity {

	@Id
	private String id;
	private String accountNumber;
	private Integer monthlyTransactionLimit;
	private Double maintenanceCommission;
	private LocalDateTime withdrawalDepositDate;
	private LocalDateTime createDate;
	private Double amount;
	private List<String> customerId;
	private List<AuthorizedSignatoryRequest> authorizedSignatory;
	private String accountType;
}
