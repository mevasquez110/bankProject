package com.nttdata.bank.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nttdata.bank.entity.CustomerEntity;
import com.nttdata.bank.mapper.CustomerMapper;
import com.nttdata.bank.repository.CustomerRepository;
import com.nttdata.bank.request.CustomerRequest;
import com.nttdata.bank.response.CustomerResponse;
import com.nttdata.bank.service.CustomerService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

	private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

	@Autowired
	private CustomerRepository customerRepository;

	/**
	 * Creates a new customer.
	 *
	 * @param customerRequest The customer request payload
	 * @return The customer response
	 */
	@Override
	public CustomerResponse createCustomer(CustomerRequest customerRequest) {
		logger.debug("Creating customer: {}", customerRequest);
		if (customerRepository.existsByDocumentNumberAndIsActiveTrue(customerRequest.getDocumentNumber())) {
			logger.warn("The document is already registered: {}", customerRequest.getDocumentNumber());
			throw new IllegalArgumentException("The document is already registered");
		}

		CustomerEntity customerEntity = CustomerMapper.mapperToEntity(customerRequest);
		customerEntity.setIsActive(true);
		customerEntity.setCreateDate(LocalDateTime.now());
		customerEntity = customerRepository.save(customerEntity);
		CustomerResponse response = CustomerMapper.mapperToResponse(customerEntity);
		logger.info("Customer created successfully: {}", response);
		return response;
	}

	/**
	 * Retrieves a customer by their document number.
	 *
	 * @param documentNumber The document number of the customer
	 * @return The customer response
	 */
	@Override
	public CustomerResponse getCustomerByDocumentNumber(String documentNumber) {
		logger.debug("Retrieving customer by document number: {}", documentNumber);
		CustomerEntity customerEntity = customerRepository.findByDocumentNumberAndIsActiveTrue(documentNumber);

		if (customerEntity == null) {
			logger.error("Customer not found with document number: {}", documentNumber);
			throw new IllegalArgumentException("Customer not found with document number: " + documentNumber);
		}

		CustomerResponse response = CustomerMapper.mapperToResponse(customerEntity);
		logger.info("Customer retrieved successfully by document number: {}", documentNumber);
		return response;
	}

	/**
	 * Retrieves all customers.
	 *
	 * @return A list of customer responses
	 */
	@Override
	public List<CustomerResponse> findAllCustomers() {
		logger.debug("Finding all customers");
		List<CustomerResponse> customers = customerRepository.findByIsActiveTrue().stream()
				.map(CustomerMapper::mapperToResponse).collect(Collectors.toList());
		logger.info("All customers retrieved successfully");
		return customers;
	}

	/**
	 * Updates a customer.
	 *
	 * @param customerId      The ID of the customer to update
	 * @param customerRequest The customer request payload containing update details
	 * @return The updated customer response
	 */
	@Override
	public CustomerResponse updateCustomer(String customerId, CustomerRequest customerRequest) {
		logger.debug("Updating customer with ID: {}", customerId);
		CustomerEntity customerEntity = customerRepository.findByIdAndIsActiveTrue(customerId).orElseThrow(() -> {
			logger.error("Customer not found with ID: {}", customerId);
			return new RuntimeException("Customer not found");
		});

		validateUpdateFields(customerRequest, customerEntity);
		customerEntity.setPhoneNumber(customerRequest.getPhoneNumber());
		customerEntity.setAddress(customerRequest.getAddress());
		customerEntity.setEmail(customerRequest.getEmail());
		customerEntity.setUpdateDate(LocalDateTime.now());
		customerEntity = customerRepository.save(customerEntity);
		CustomerResponse response = CustomerMapper.mapperToResponse(customerEntity);
		logger.info("Customer updated successfully: {}", response);
		return response;
	}

	/**
	 * Validates the update fields for a customer.
	 *
	 * @param customerRequest The customer request payload containing update details
	 * @param customerEntity  The customer entity
	 */
	private void validateUpdateFields(CustomerRequest customerRequest, CustomerEntity customerEntity) {
		if (!customerEntity.getIsActive()) {
			throw new IllegalArgumentException("Customer not found");
		}
		if (!customerRequest.getFullName().equals(customerEntity.getFullName())) {
			throw new IllegalArgumentException("Full name is not editable");
		}
		if (!customerRequest.getCompanyName().equals(customerEntity.getCompanyName())) {
			throw new IllegalArgumentException("Company name is not editable");
		}
		if (!customerRequest.getDocumentType().equals(customerEntity.getDocumentType())) {
			throw new IllegalArgumentException("Document type is not editable");
		}
		if (!customerRequest.getDocumentNumber().equals(customerEntity.getDocumentNumber())) {
			throw new IllegalArgumentException("Document number is not editable");
		}
		if (!customerRequest.getPersonType().equals(customerEntity.getPersonType())) {
			throw new IllegalArgumentException("Person type is not editable");
		}
	}

	/**
	 * Deletes a customer.
	 *
	 * @param customerId The ID of the customer to delete
	 */
	@Override
	public void deleteCustomer(String customerId) {
		logger.debug("Deleting customer with ID: {}", customerId);
		CustomerEntity customerEntity = customerRepository.findByIdAndIsActiveTrue(customerId).orElseThrow(() -> {
			logger.error("Customer not found with ID: {}", customerId);
			return new RuntimeException("Customer not found");
		});

		customerEntity.setIsActive(false);
		customerEntity.setDeleteDate(LocalDateTime.now());
		customerEntity = customerRepository.save(customerEntity);
		logger.info("Customer deleted successfully with ID: {}", customerId);
	}
}
