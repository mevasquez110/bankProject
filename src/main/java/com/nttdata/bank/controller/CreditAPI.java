package com.nttdata.bank.controller;

import javax.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bank.request.CreditRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.CreditResponse;
import com.nttdata.bank.response.CreditDebtResponse;

@RestController
@RequestMapping("/credits")
public interface CreditAPI {

	@PostMapping("/grant")
	ApiResponse<CreditResponse> grantCredit(@RequestBody @Valid CreditRequest creditRequest);

	@GetMapping("/debt/{creditId}")
	ApiResponse<CreditDebtResponse> checkDebt(@PathVariable String creditId);
}
