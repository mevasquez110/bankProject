package com.nttdata.bank.service;

import java.util.List;

import com.nttdata.bank.request.ContactDataRequest;
import com.nttdata.bank.request.CustomerRequest;
import com.nttdata.bank.response.CustomerResponse;

/**
 * CustomerService provides the service layer for handling customer-related operations.
 * This interface defines methods for creating a customer, getting customer details by document number,
 * finding all customers, updating a customer, and deleting a customer.
 */

public interface CustomerService {

    /**
     * Creates a new customer.
     *
     * @param customerRequest The customer request to create a new customer
     * @return The response containing customer details
     */
    CustomerResponse createCustomer(CustomerRequest customerRequest);

    /**
     * Gets customer details by document number.
     *
     * @param documentNumber The document number to search for
     * @return The response containing customer details
     */
    CustomerResponse getCustomerByDocumentNumber(String documentNumber);

    /**
     * Finds all customers.
     *
     * @return A list of responses containing customer details
     */
    List<CustomerResponse> findAllCustomers();

    /**
     * Updates an existing customer.
     *
     * @param documentNumber The document number to update
     * @param contactDataRequest The customer request with updated details
     * @return The response containing updated customer details
     */
    CustomerResponse updateCustomer(String documentNumber, ContactDataRequest contactDataRequest);

    /**
     * Deletes a customer.
     *
     * @param documentNumber The document number to delete
     */
    void deleteCustomer(String documentNumber);
}
