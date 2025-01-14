package com.nttdata.bank.controller.impl;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bank.controller.CustomersAPI;
import com.nttdata.bank.request.ContactDataRequest;
import com.nttdata.bank.request.CustomerRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.CustomerResponse;
import com.nttdata.bank.service.CustomerService;

/**
 * * CustomerController is a REST controller that implements the CustomersAPI
 * interface. * This class handles HTTP requests related to customer operations
 * such as registering customers, * searching customers by document number,
 * retrieving all customers, updating customer details, * and deleting
 * customers. It delegates the actual business logic to the appropriate service
 * layer.
 */

@RestController
public class CustomerController implements CustomersAPI {

	private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

	@Autowired
	private CustomerService customerService;

	/**
	 * Creates a new customer.
	 *
	 * @param customerRequest The customer request payload
	 * @return ApiResponse containing the customer response
	 */
	@Override
	public ApiResponse<CustomerResponse> createCustomer(CustomerRequest customerRequest) {
		logger.debug("Received request to create customer: {}", customerRequest);
		ApiResponse<CustomerResponse> response = new ApiResponse<>();
		CustomerResponse customerResponse = customerService.createCustomer(customerRequest);
		response.setStatusCode(HttpStatus.CREATED.value());
		response.setMessage("Customer registered successfully");
		response.setData(customerResponse);
		logger.info("Customer registered successfully: {}", customerResponse);
		return response;
	}

	/**
	 * Retrieves a customer by their document number. 
	 *
	 * @param documentNumber The document number of the customer
	 * @return ApiResponse containing the customer response
	 */
	@Override
	public ApiResponse<CustomerResponse> getCustomerByDocumentNumber(String documentNumber) {
		logger.debug("Received request to retrieve customer by document number: {}", documentNumber);
		ApiResponse<CustomerResponse> response = new ApiResponse<>();
		CustomerResponse customerResponse = customerService.getCustomerByDocumentNumber(documentNumber);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Customer retrieved successfully");
		response.setData(customerResponse);
		logger.info("Customer retrieved successfully by document number: {}", documentNumber);
		return response;
	}

	/**
	 * Retrieves all customers. 
	 *
	 * @return ApiResponse containing the list of customer responses
	 */
	@Override
	public ApiResponse<List<CustomerResponse>> findAllCustomers() {
		logger.debug("Received request to retrieve all customers");
		ApiResponse<List<CustomerResponse>> response = new ApiResponse<>();
		List<CustomerResponse> customers = customerService.findAllCustomers();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Customers retrieved successfully");
		response.setData(customers);
		logger.info("All customers retrieved successfully");
		return response;
	}

	/**
	 * Updates a customer. 
	 *
	 * @param documentNumber      The document number to update
	 * @param contactDataRequest The customer request payload containing update details
	 * @return ApiResponse containing the updated customer response
	 */
	@Override
	public ApiResponse<CustomerResponse> updateCustomer(String documentNumber, ContactDataRequest contactDataRequest) {
		logger.debug("Received request to update customer with document number: {}", documentNumber);
		ApiResponse<CustomerResponse> response = new ApiResponse<>();
		CustomerResponse customerResponse = customerService.updateCustomer(documentNumber, contactDataRequest);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Customer updated successfully");
		response.setData(customerResponse);
		logger.info("Customer updated successfully with document number: {}", documentNumber);
		return response;
	}

	/**
	 * Deletes a customer. 
	 *
	 * @param documentNumber The document number to delete
	 * @return ApiResponse indicating the result of the delete operation
	 */
	@Override
	public ApiResponse<Void> deleteCustomer(String documentNumber) {
		logger.debug("Received request to delete customer with document number: {}", documentNumber);
		ApiResponse<Void> response = new ApiResponse<>();
		customerService.deleteCustomer(documentNumber);
		response.setStatusCode(HttpStatus.NO_CONTENT.value());
		response.setMessage("Customer deleted successfully");
		logger.info("Customer deleted successfully with document number: {}", documentNumber);
		return response;
	}
}
