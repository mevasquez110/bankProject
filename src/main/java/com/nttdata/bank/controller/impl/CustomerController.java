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
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

/**
 * CustomerController is a REST controller that implements the CustomersAPI
 * interface. This class handles HTTP requests related to customer operations
 * such as registering customers, searching customers by document number,
 * retrieving all customers, updating customer details, and deleting customers.
 * It delegates the actual business logic to the CustomerService.
 * 
 * It also uses Resilience4j annotations (@CircuitBreaker and @TimeLimiter) to
 * provide resilience in case of failures or timeouts, and includes fallback
 * methods to handle these scenarios gracefully.
 */
@RestController
public class CustomerController implements CustomersAPI {

	private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

	@Autowired
	private CustomerService customerService;

	/**
	 * Registers a new customer based on the provided CustomerRequest object.
	 * Utilizes CircuitBreaker and TimeLimiter to handle resilience.
	 *
	 * @param customerRequest - The customer details provided in the request body.
	 * @return ApiResponse containing the registered CustomerResponse.
	 */
	@Override
	@CircuitBreaker(name = "customerService", fallbackMethod = "fallbackCreateCustomer")
	@TimeLimiter(name = "customerService")
	public ApiResponse<CustomerResponse> createCustomer(CustomerRequest customerRequest) {
		logger.debug("Received request to create customer: {}", customerRequest);
		ApiResponse<CustomerResponse> response = new ApiResponse<>();
		CustomerResponse customerResponse = customerService.createCustomer(customerRequest);
		response.setStatusCode(HttpStatus.CREATED.value());
		response.setMessage("Customer registered successfully.");
		response.setData(customerResponse);
		logger.info("Customer registered successfully: {}", customerResponse);
		return response;
	}

	/**
	 * Searches for a customer by their document number. Utilizes CircuitBreaker and
	 * TimeLimiter to handle resilience.
	 *
	 * @param documentNumber - The document number of the customer to search for.
	 * @return ApiResponse containing the CustomerResponse.
	 */
	@Override
	@CircuitBreaker(name = "customerService", fallbackMethod = "fallbackGetCustomerByDocumentNumber")
	@TimeLimiter(name = "customerService")
	public ApiResponse<CustomerResponse> getCustomerByDocumentNumber(String documentNumber) {
		logger.debug("Received request to retrieve customer by document number: {}", documentNumber);
		ApiResponse<CustomerResponse> response = new ApiResponse<>();
		CustomerResponse customerResponse = customerService.getCustomerByDocumentNumber(documentNumber);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Customer retrieved successfully.");
		response.setData(customerResponse);
		logger.info("Customer retrieved successfully by document number: {}", documentNumber);
		return response;
	}

	/**
	 * Retrieves a list of all customers. Utilizes CircuitBreaker and TimeLimiter to
	 * handle resilience.
	 *
	 * @return ApiResponse containing a list of CustomerResponse objects.
	 */
	@Override
	@CircuitBreaker(name = "customerService", fallbackMethod = "fallbackFindAllCustomers")
	@TimeLimiter(name = "customerService")
	public ApiResponse<List<CustomerResponse>> findAllCustomers() {
		logger.debug("Received request to retrieve all customers.");
		ApiResponse<List<CustomerResponse>> response = new ApiResponse<>();
		List<CustomerResponse> customers = customerService.findAllCustomers();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Customers retrieved successfully.");
		response.setData(customers);
		logger.info("All customers retrieved successfully.");
		return response;
	}

	/**
	 * Updates the specified customer based on their document number. Utilizes
	 * CircuitBreaker and TimeLimiter to handle resilience.
	 *
	 * @param documentNumber     - The document number of the customer to be
	 *                           updated.
	 * @param contactDataRequest - The updated customer details provided in the
	 *                           request body.
	 * @return ApiResponse containing the updated CustomerResponse.
	 */
	@Override
	@CircuitBreaker(name = "customerService", fallbackMethod = "fallbackUpdateCustomer")
	@TimeLimiter(name = "customerService")
	public ApiResponse<CustomerResponse> updateCustomer(String documentNumber, ContactDataRequest contactDataRequest) {
		logger.debug("Received request to update customer with document number: {}", documentNumber);
		ApiResponse<CustomerResponse> response = new ApiResponse<>();
		CustomerResponse customerResponse = customerService.updateCustomer(documentNumber, contactDataRequest);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Customer updated successfully.");
		response.setData(customerResponse);
		logger.info("Customer updated successfully with document number: {}", documentNumber);
		return response;
	}

	/**
	 * Deletes the specified customer based on their document number. Utilizes
	 * CircuitBreaker and TimeLimiter to handle resilience.
	 *
	 * @param documentNumber - The document number of the customer to be deleted.
	 * @return ApiResponse with a status message upon successful deletion.
	 */
	@Override
	@CircuitBreaker(name = "customerService", fallbackMethod = "fallbackDeleteCustomer")
	@TimeLimiter(name = "customerService")
	public ApiResponse<Void> deleteCustomer(String documentNumber) {
		logger.debug("Received request to delete customer with document number: {}", documentNumber);
		ApiResponse<Void> response = new ApiResponse<>();
		customerService.deleteCustomer(documentNumber);
		response.setStatusCode(HttpStatus.NO_CONTENT.value());
		response.setMessage("Customer deleted successfully.");
		logger.info("Customer deleted successfully with document number: {}", documentNumber);
		return response;
	}

	/**
	 * Fallback method for createCustomer in case of failure or timeout.
	 *
	 * @param customerRequest - The original customer request.
	 * @param throwable       - The exception that caused the fallback to be
	 *                        triggered.
	 * @return ApiResponse indicating failure to create customer.
	 */
	public ApiResponse<CustomerResponse> fallbackCreateCustomer(CustomerRequest customerRequest, Throwable throwable) {
		logger.error("Fallback method for createCustomer due to: {}", throwable.getMessage());
		ApiResponse<CustomerResponse> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setMessage("Unable to create customer at the moment. Please try again later.");
		return response;
	}

	/**
	 * Fallback method for getCustomerByDocumentNumber in case of failure or
	 * timeout.
	 *
	 * @param documentNumber - The document number being retrieved.
	 * @param throwable      - The exception that caused the fallback to be
	 *                       triggered.
	 * @return ApiResponse indicating failure to retrieve customer.
	 */
	public ApiResponse<CustomerResponse> fallbackGetCustomerByDocumentNumber(String documentNumber,
			Throwable throwable) {
		logger.error("Fallback method for getCustomerByDocumentNumber due to: {}", throwable.getMessage());
		ApiResponse<CustomerResponse> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setMessage("Unable to retrieve customer at the moment. Please try again later.");
		return response;
	}

	/**
	 * Fallback method for findAllCustomers in case of failure or timeout.
	 *
	 * @param throwable - The exception that caused the fallback to be triggered.
	 * @return ApiResponse indicating failure to retrieve customers.
	 */
	public ApiResponse<List<CustomerResponse>> fallbackFindAllCustomers(Throwable throwable) {
		logger.error("Fallback method for findAllCustomers due to: {}", throwable.getMessage());
		ApiResponse<List<CustomerResponse>> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setMessage("Unable to retrieve customers at the moment. Please try again later.");
		return response;
	}

	/**
	 * Fallback method for updateCustomer in case of failure or timeout.
	 *
	 * @param documentNumber     - The document number of the customer being
	 *                           updated.
	 * @param contactDataRequest - The updated customer request.
	 * @param throwable          - The exception that caused the fallback to be
	 *                           triggered.
	 * @return ApiResponse indicating failure to update customer.
	 */
	public ApiResponse<CustomerResponse> fallbackUpdateCustomer(String documentNumber,
			ContactDataRequest contactDataRequest, Throwable throwable) {
		logger.error("Fallback method for updateCustomer due to: {}", throwable.getMessage());
		ApiResponse<CustomerResponse> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setMessage("Unable to update customer at the moment. Please try again later.");
		return response;
	}

	/**
	 * Fallback method for deleteCustomer in case of failure or timeout.
	 *
	 * @param documentNumber - The document number of the customer being deleted.
	 * @param throwable      - The exception that caused the fallback to be
	 *                       triggered.
	 * @return ApiResponse indicating failure to delete customer.
	 */
	public ApiResponse<Void> fallbackDeleteCustomer(String documentNumber, Throwable throwable) {
		logger.error("Fallback method for deleteCustomer due to: {}", throwable.getMessage());
		ApiResponse<Void> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setMessage("Unable to delete customer at the moment. Please try again later.");
		return response;
	}
}
