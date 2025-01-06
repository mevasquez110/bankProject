package com.nttdata.bank.request;

import java.util.List;
import com.nttdata.bank.bean.AuthorizedSignatory;
import lombok.Data;

@Data
public class AccountRequest {

	private Integer accountId;
	private List<Integer> customerId;
	private List<AuthorizedSignatory> authorizedSignatory;
}
