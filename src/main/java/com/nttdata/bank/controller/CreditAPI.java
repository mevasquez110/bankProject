package com.nttdata.bank.controller;

import javax.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.nttdata.bank.request.CreditRequest;
import com.nttdata.bank.request.ReprogramDebtRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.CreditResponse;
import com.nttdata.bank.response.CreditDebtResponse;
import java.util.List;

@RestController
@RequestMapping("/credits")
public interface CreditAPI {

	@PostMapping("/grant")
	ApiResponse<CreditResponse> grantCredit(@RequestBody @Valid CreditRequest creditRequest);

	@GetMapping("/debt/{creditId}")
	ApiResponse<CreditDebtResponse> checkDebt(@PathVariable String creditId);

	@PutMapping("/reprogram")
	ApiResponse<CreditResponse> updateReprogramDebt(@RequestBody @Valid ReprogramDebtRequest reprogramDebtRequest);

	@GetMapping("/all")
	ApiResponse<List<CreditResponse>> findAllCredits();

	@DeleteMapping("/{creditId}")
	ApiResponse<Void> deleteCredit(@PathVariable String creditId);
}
