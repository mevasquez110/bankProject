package com.nttdata.bank.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bank.controller.CustomersAPI;
import com.nttdata.bank.request.CustomerRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.CustomerResponse;
import com.nttdata.bank.service.CustomerService;

@RestController
public class CustomerController implements CustomersAPI {

	@Autowired
	private CustomerService customerService;

	@Override
	public ApiResponse<CustomerResponse> registerCustomer(CustomerRequest customerRequest) {
		ApiResponse<CustomerResponse> response = new ApiResponse<>();
		CustomerResponse customerResponse = customerService.registerCustomer(customerRequest);
		response.setStatusCode(HttpStatus.CREATED.value());
		response.setMessage("Customer registered successfully");
		response.setData(customerResponse);
		return response;
	}

	@Override
	public ApiResponse<CustomerResponse> getCustomerById(String customerId) {
		ApiResponse<CustomerResponse> response = new ApiResponse<>();
		CustomerResponse customerResponse = customerService.getCustomerById(customerId);

		if (customerResponse != null) {
			response.setStatusCode(HttpStatus.OK.value());
			response.setMessage("Customer retrieved successfully");
			response.setData(customerResponse);
		} else {
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			response.setMessage("Customer not found");
		}

		return response;
	}
}
