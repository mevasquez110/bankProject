package com.nttdata.bank.controller;

import java.util.List;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bank.request.AccountTransferRequest;
import com.nttdata.bank.request.ConsumptionRequest;
import com.nttdata.bank.request.DepositRequest;
import com.nttdata.bank.request.MobileTransferRequest;
import com.nttdata.bank.request.PayCreditCardRequest;
import com.nttdata.bank.request.PayCreditRequest;
import com.nttdata.bank.request.WithdrawalRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.TransactionResponse;

@RestController
@RequestMapping("/transactions")
public interface TransactionAPI {

	@PostMapping("/deposit")
	ApiResponse<TransactionResponse> makeDeposit(@RequestBody @Valid DepositRequest depositRequest);

	@PostMapping("/withdraw")
	ApiResponse<TransactionResponse> makeWithdrawal(@RequestBody @Valid WithdrawalRequest withdrawalRequest);

	@PostMapping("/account-transfer")
	ApiResponse<TransactionResponse> makeAccountTransfer(
			@RequestBody @Valid AccountTransferRequest accountTransferRequest);

	@PostMapping("/mobile-transfer")
	ApiResponse<TransactionResponse> makeMobileTransfer(
			@RequestBody @Valid MobileTransferRequest mobileTransferRequest);

	@PostMapping("/pay-credit-card")
	ApiResponse<TransactionResponse> payCreditCard(@RequestBody @Valid PayCreditCardRequest payCreditCardRequest);

	@PostMapping("/pay-credit")
	ApiResponse<TransactionResponse> payCredit(@RequestBody @Valid PayCreditRequest payCreditRequest);

	@PostMapping("/charge")
	ApiResponse<TransactionResponse> chargeConsumption(@RequestBody @Valid ConsumptionRequest consumptionRequest);

	@GetMapping("/{accountId}")
	ApiResponse<List<TransactionResponse>> checkTransactions(@PathVariable Integer accountId);
}
