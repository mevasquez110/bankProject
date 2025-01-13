package com.nttdata.bank.controller;

import java.util.List;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bank.request.DebitCardRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.DebitCardResponse;

@RestController
@RequestMapping("/debit-card")
public interface DebitCardAPI {

	@PostMapping("/create")
	ApiResponse<DebitCardResponse> createDebitCard(@RequestBody @Valid DebitCardRequest debitCardRequest);

	@GetMapping("/all")
	ApiResponse<List<DebitCardResponse>> findAllDebitCard();

	@PutMapping("/update/{documentNumber}")
	ApiResponse<DebitCardResponse> update(@PathVariable String documentNumber);

	@DeleteMapping("/delete/{accountNumber}")
	ApiResponse<Void> deleteAccount(@PathVariable String phoneNumber);
}
