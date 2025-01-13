package com.nttdata.bank.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nttdata.bank.entity.CustomerEntity;
import com.nttdata.bank.mapper.CustomerMapper;
import com.nttdata.bank.repository.CustomerRepository;
import com.nttdata.bank.request.ContactDataRequest;
import com.nttdata.bank.request.CustomerRequest;
import com.nttdata.bank.response.CustomerResponse;
import com.nttdata.bank.service.CustomerService;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.util.List;

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
		existsPhoneNumber(customerRequest.getPhoneNumber());
		existsDocumentNumber(customerRequest.getDocumentNumber());
		CustomerEntity customerEntity = CustomerMapper.mapperToEntity(customerRequest);
		customerEntity.setIsActive(true);
		customerEntity.setCreateDate(LocalDateTime.now());
		CustomerEntity savedEntity = customerRepository.save(customerEntity).toFuture().join();
		CustomerResponse response = CustomerMapper.mapperToResponse(savedEntity);
		logger.info("Customer created successfully: {}", response);
		return response;
	}

	private void existsDocumentNumber(String documentNumber) {
		Boolean existsDocumentNumber = customerRepository.existsByDocumentNumberAndIsActiveTrue(documentNumber)
				.toFuture().join();

		if (existsDocumentNumber) {
			logger.warn("The document is already registered: {}", documentNumber);
			throw new IllegalArgumentException("The document is already registered");
		}
	}

	private void existsPhoneNumber(String phoneNumber) {
		Boolean existsPhoneNumber = customerRepository.existsByPhoneNumberAndIsActiveTrue(phoneNumber)
				.toFuture().join();

		if (existsPhoneNumber) {
			logger.warn("The phone is already registered: {}", phoneNumber);
			throw new IllegalArgumentException("The phone number is already registered");
		}
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

		Mono<CustomerEntity> customerEntityMono = customerRepository.findByDocumentNumberAndIsActiveTrue(documentNumber)
				.switchIfEmpty(Mono.error(
						new IllegalArgumentException("Customer not found with document: " + documentNumber)));

		CustomerEntity customerEntity = customerEntityMono.toFuture().join();
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

		List<CustomerResponse> customers = customerRepository.findByIsActiveTrue().map(CustomerMapper::mapperToResponse)
				.collectList().toFuture().join();

		logger.info("All customers retrieved successfully");
		return customers;
	}

	/**
	 * Updates a customer. 
	 *
	 * @param documentNumber  The document number to update
	 * @param contactDataRequest The customer request payload containing update details
	 * @return The updated customer response
	 */
	@Override
	public CustomerResponse updateCustomer(String documentNumber, ContactDataRequest contactDataRequest) {
		logger.debug("Updating customer with document number: {}", documentNumber);
		existsPhoneNumber(contactDataRequest.getPhoneNumber());
		return customerRepository.findByDocumentNumberAndIsActiveTrue(documentNumber)
				.switchIfEmpty(Mono.error(new IllegalArgumentException("Customer not found"))).map(customerEntity -> {
					customerEntity.setPhoneNumber(contactDataRequest.getPhoneNumber());
					customerEntity.setAddress(contactDataRequest.getAddress());
					customerEntity.setEmail(contactDataRequest.getEmail());
					customerEntity.setUpdateDate(LocalDateTime.now());
					return customerEntity;
				}).flatMap(customerRepository::save).map(CustomerMapper::mapperToResponse)
				.doOnSuccess(response -> logger.info("Customer updated successfully: {}", response)).toFuture().join();
	}

	/**
	 * Deletes a customer. 
	 *
	 * @param documentNumber The document number to delete
	 */
	@Override
	public void deleteCustomer(String documentNumber) {
		logger.debug("Deleting customer with document number: {}", documentNumber);
		customerRepository.findByDocumentNumberAndIsActiveTrue(documentNumber)
				.switchIfEmpty(Mono.error(new IllegalArgumentException("Customer not found"))).map(customerEntity -> {
					customerEntity.setIsActive(false);
					customerEntity.setDeleteDate(LocalDateTime.now());
					return customerEntity;
				}).flatMap(customerRepository::save).doOnSuccess(customerEntity -> logger
						.info("Customer deleted successfully with document number: {}", documentNumber))
				.toFuture().join();
	}
}
