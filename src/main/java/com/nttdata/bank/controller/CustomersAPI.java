package com.nttdata.bank.controller;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bank.request.CustomerRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.CustomerResponse;

@RestController
@RequestMapping("/customers")
public interface CustomersAPI {

	@PostMapping("/register")
	ApiResponse<CustomerResponse> registerCustomer(@RequestBody @Valid CustomerRequest customerRequest);

	@GetMapping("/search")
	ApiResponse<CustomerResponse> getCustomerByDocumentNumber(@RequestParam String documentNumber);
}
