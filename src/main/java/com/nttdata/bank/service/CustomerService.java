package com.nttdata.bank.service;

import com.nttdata.bank.request.CustomerRequest;
import com.nttdata.bank.response.CustomerResponse;

public interface CustomerService {

	CustomerResponse registerCustomer(CustomerRequest customer);

	CustomerResponse getCustomerByDocumentNumber(String documentNumber);

}
