package com.nttdata.bank.controller;

import javax.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bank.request.CustomerRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.CustomerResponse;

@RestController
@RequestMapping("/customers")
public interface CustomersAPI {

	@PostMapping("/personal")
	ApiResponse<CustomerResponse> registerCustomer(@RequestBody @Valid  CustomerRequest customerRequest);

	@GetMapping("/{customerId}")
	ApiResponse<CustomerResponse> getCustomerById(@PathVariable String customerId);
}
