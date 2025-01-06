package com.nttdata.bank.response;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountResponse {
	
    private String accountNumber;
    private Integer monthlyTransactionLimit;
    private Double maintenanceCommission;
    private LocalDateTime withdrawalDepositDate;
	private LocalDateTime createDate;

}
