package com.nttdata.bank.controller.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bank.controller.CustomersAPI;
import com.nttdata.bank.request.CustomerRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.CustomerResponse;
import com.nttdata.bank.service.CustomerService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;

@RestController
public class CustomerController implements CustomersAPI {

	private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

	@Autowired
	private CustomerService customerService;

	/**
	 * Creates a new customer. This method is transactional and uses a circuit
	 * breaker for resilience.
	 *
	 * @param customerRequest The customer request payload
	 * @return ApiResponse containing the customer response
	 */
	@Override
	@Transactional
	@CircuitBreaker(name = "customerService", fallbackMethod = "fallbackCreateCustomer")
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
	 * Fallback method for createCustomer in case of failure.
	 *
	 * @param customerRequest The customer request payload
	 * @param t               The throwable cause of the failure
	 * @return ApiResponse containing the fallback response
	 */
	public ApiResponse<CustomerResponse> fallbackCreateCustomer(CustomerRequest customerRequest, Throwable t) {
		logger.error("Fallback method triggered for createCustomer due to: {}", t.getMessage());
		ApiResponse<CustomerResponse> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE.value());
		response.setMessage("Service is currently unavailable. Please try again later.");
		return response;
	}

	/**
	 * Retrieves a customer by their document number. This method is transactional
	 * and uses a circuit breaker for resilience.
	 *
	 * @param documentNumber The document number of the customer
	 * @return ApiResponse containing the customer response
	 */
	@Override
	@Transactional
	@CircuitBreaker(name = "customerService")
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
	 * Retrieves all customers. This method is transactional and uses a circuit
	 * breaker for resilience.
	 *
	 * @return ApiResponse containing the list of customer responses
	 */
	@Transactional
	@CircuitBreaker(name = "customerService")
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
	 * Updates a customer. This method is transactional and uses a circuit breaker
	 * for resilience.
	 *
	 * @param customerId      The ID of the customer to update
	 * @param customerRequest The customer request payload containing update details
	 * @return ApiResponse containing the updated customer response
	 */
	@Transactional
	@CircuitBreaker(name = "customerService")
	public ApiResponse<CustomerResponse> updateCustomer(String customerId, CustomerRequest customerRequest) {
		logger.debug("Received request to update customer with ID: {}", customerId);
		ApiResponse<CustomerResponse> response = new ApiResponse<>();
		CustomerResponse customerResponse = customerService.updateCustomer(customerId, customerRequest);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Customer updated successfully");
		response.setData(customerResponse);
		logger.info("Customer updated successfully with ID: {}", customerId);
		return response;
	}

	/**
	 * Deletes a customer. This method is transactional and uses a circuit breaker
	 * for resilience.
	 *
	 * @param customerId The ID of the customer to delete
	 * @return ApiResponse indicating the result of the delete operation
	 */
	@Transactional
	@CircuitBreaker(name = "customerService")
	public ApiResponse<Void> deleteCustomer(String customerId) {
		logger.debug("Received request to delete customer with ID: {}", customerId);
		ApiResponse<Void> response = new ApiResponse<>();
		customerService.deleteCustomer(customerId);
		response.setStatusCode(HttpStatus.NO_CONTENT.value());
		response.setMessage("Customer deleted successfully");
		logger.info("Customer deleted successfully with ID: {}", customerId);
		return response;
	}
}
