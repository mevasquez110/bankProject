package com.nttdata.bank.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bank.controller.CustomersAPI;
import com.nttdata.bank.request.CustomerRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.CustomerResponse;
import com.nttdata.bank.service.CustomerService;
import java.util.List;

@RestController
public class CustomerController implements CustomersAPI {

	@Autowired
	private CustomerService customerService;

	@Override
	public ApiResponse<CustomerResponse> createCustomer(CustomerRequest customerRequest) {
		ApiResponse<CustomerResponse> response = new ApiResponse<>();
		CustomerResponse customerResponse = customerService.createCustomer(customerRequest);
		response.setStatusCode(HttpStatus.CREATED.value());
		response.setMessage("Customer registered successfully");
		response.setData(customerResponse);
		return response;
	}

	@Override
	public ApiResponse<CustomerResponse> getCustomerByDocumentNumber(String documentNumber) {
		ApiResponse<CustomerResponse> response = new ApiResponse<>();
		CustomerResponse customerResponse = customerService.getCustomerByDocumentNumber(documentNumber);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Customer retrieved successfully");
		response.setData(customerResponse);
		return response;
	}

	public ApiResponse<List<CustomerResponse>> findAllCustomers() {
		ApiResponse<List<CustomerResponse>> response = new ApiResponse<>();
		List<CustomerResponse> customers = customerService.findAllCustomers();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Customers retrieved successfully");
		response.setData(customers);
		return response;
	}

	public ApiResponse<CustomerResponse> updateCustomer(String customerId, CustomerRequest customerRequest) {
		ApiResponse<CustomerResponse> response = new ApiResponse<>();
		CustomerResponse customerResponse = customerService.updateCustomer(customerId, customerRequest);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Customer updated successfully");
		response.setData(customerResponse);
		return response;
	}

	public ApiResponse<Void> deleteCustomer(String customerId) {
		ApiResponse<Void> response = new ApiResponse<>();
		customerService.deleteCustomer(customerId);
		response.setStatusCode(HttpStatus.NO_CONTENT.value());
		response.setMessage("Customer deleted successfully");
		return response;
	}
}
