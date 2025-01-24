package com.nttdata.bank.job.bean;

import java.util.List;
import com.nttdata.bank.response.ProductResponse;
import com.nttdata.bank.response.TransactionResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The ProductBalance class extends ProductResponse and represents the balance
 * of financial products. It includes detailed information about the average
 * balance, amount balance, average commission, credit amount, credit card
 * amount, and a list of associated transactions. Utilizes Lombok annotations
 * for automatic generation of getters and setters, and includes additional
 * properties necessary for balance and commission calculations.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ProductBalance extends ProductResponse {

	private Double averageBalance;
	private Double amountBalance;
	private Double averageCommission;
	private Double creditAmount;
	private Double creditCardAmount;
	private List<TransactionResponse> transactions;
}
