package com.nttdata.bank.controller;

import javax.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.nttdata.bank.request.CustomerRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.CustomerResponse;
import java.util.List;

/**
 * CustomersAPI defines the RESTful endpoints for customer-related operations.
 * This interface includes methods for registering customers, searching
 * customers by document number, retrieving all customers, updating customer
 * details, and deleting customers. Each method maps to an HTTP request and
 * returns a structured API response.
 */

@RestController
@RequestMapping("/customers")
public interface CustomersAPI {

	/**
	 * Registers a new customer based on the provided CustomerRequest object.
	 * 
	 * @param customerRequest - The customer details provided in the request body.
	 * @return ApiResponse containing the registered CustomerResponse.
	 */
	@PostMapping("/register")
	ApiResponse<CustomerResponse> createCustomer(@RequestBody @Valid CustomerRequest customerRequest);

	/**
	 * Searches for a customer by their document number.
	 * 
	 * @param documentNumber - The document number of the customer to search for.
	 * @return ApiResponse containing the CustomerResponse.
	 */
	@GetMapping("/search")
	ApiResponse<CustomerResponse> getCustomerByDocumentNumber(@RequestParam String documentNumber);

	/**
	 * Retrieves a list of all customers.
	 * 
	 * @return ApiResponse containing a list of CustomerResponse objects.
	 */
	@GetMapping("/all")
	ApiResponse<List<CustomerResponse>> findAllCustomers();

	/**
	 * Updates the details of a specified customer based on their ID.
	 * 
	 * @param documentNumber - The document number to be updated.
	 * @param customerRequest - The updated customer details provided in the request
	 *                        body.
	 * @return ApiResponse containing the updated CustomerResponse.
	 */
	@PutMapping("/update/{documentNumber}")
	ApiResponse<CustomerResponse> updateCustomer(@PathVariable String documentNumber,
			@RequestBody @Valid CustomerRequest customerRequest);

	/**
	 * Deletes a specified customer based on their ID.
	 * 
	 * @param documentNumber - The document number to be deleted.
	 * @return ApiResponse with no content upon successful deletion.
	 */
	@DeleteMapping("/delete/{documentNumber}")
	ApiResponse<Void> deleteCustomer(@PathVariable String documentNumber);
}
