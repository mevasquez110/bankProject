package com.nttdata.bank.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bank.request.CreditRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.CreditResponse;
import com.nttdata.bank.response.DebtResponse;

@RestController
@RequestMapping("/credits")
public interface CreditAPI {

	@PostMapping("/personal")
	ApiResponse<CreditResponse> grantPersonalCredit(@RequestBody CreditRequest creditRequest);

	@PostMapping("/business")
	ApiResponse<CreditResponse> grantBusinessCredit(@RequestBody CreditRequest creditRequest);

	@GetMapping("/debt/{creditId}")
	ApiResponse<DebtResponse> checkDebt(@PathVariable int creditId);
}
