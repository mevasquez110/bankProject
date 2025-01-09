package com.nttdata.bank.controller;

import java.util.List;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bank.request.TransactionRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.TransactionResponse;

@RestController
@RequestMapping("/transactions")
public interface TransactionAPI {

	@PostMapping("/deposit")
	ApiResponse<TransactionResponse> makeDeposit(@RequestBody @Valid TransactionRequest transaction);

	@PostMapping("/withdraw")
	ApiResponse<TransactionResponse> makeWithdrawal(@RequestBody @Valid TransactionRequest transaction);

	@PostMapping("/pay-installment")
	ApiResponse<TransactionResponse> payInstallment(@RequestBody @Valid TransactionRequest transaction);

	@GetMapping("/{accountId}")
	ApiResponse<List<TransactionResponse>> checkTransactions(@PathVariable Integer accountId);

	@PostMapping("/charge")
	ApiResponse<TransactionResponse> chargeConsumption(@RequestBody @Valid TransactionRequest transaction);
}
