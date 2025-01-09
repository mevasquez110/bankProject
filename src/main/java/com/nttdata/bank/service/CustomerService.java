package com.nttdata.bank.service;

import java.util.List;

import com.nttdata.bank.request.CustomerRequest;
import com.nttdata.bank.response.CustomerResponse;

public interface CustomerService {

	CustomerResponse createCustomer(CustomerRequest customer);

	CustomerResponse getCustomerByDocumentNumber(String documentNumber);

	List<CustomerResponse> findAllCustomers();

	CustomerResponse updateCustomer(String customerId, CustomerRequest customerRequest);

	void deleteCustomer(String customerId);
}
