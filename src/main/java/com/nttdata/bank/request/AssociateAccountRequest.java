package com.nttdata.bank.request;

import java.util.List;
import lombok.Data;

@Data
public class AssociateAccountRequest {

	private List<String> associatedAccounts;

	private String primaryAccount;
}
