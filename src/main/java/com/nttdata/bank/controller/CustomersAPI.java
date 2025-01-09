package com.nttdata.bank.controller;

import javax.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.nttdata.bank.request.CustomerRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.CustomerResponse;
import java.util.List;

@RestController
@RequestMapping("/customers")
public interface CustomersAPI {

	@PostMapping("/register")
	ApiResponse<CustomerResponse> createCustomer(@RequestBody @Valid CustomerRequest customerRequest);

	@GetMapping("/search")
	ApiResponse<CustomerResponse> getCustomerByDocumentNumber(@RequestParam String documentNumber);

	@GetMapping("/all")
	ApiResponse<List<CustomerResponse>> findAllCustomers();

	@PutMapping("/update/{customerId}")
	ApiResponse<CustomerResponse> updateCustomer(@PathVariable String customerId,
			@RequestBody @Valid CustomerRequest customerRequest);

	@DeleteMapping("/delete/{customerId}")
	ApiResponse<Void> deleteCustomer(@PathVariable String customerId);
}
