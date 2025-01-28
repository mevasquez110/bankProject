package com.nttdata.bank.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nttdata.bank.auth.service.impl.AuthenticationServiceImpl;
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
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

	private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private AuthenticationServiceImpl authenticationService;

	public String createTokenForUser(String username) {
		return authenticationService.generateJwt(username);
	}

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
		CustomerEntity savedEntity = customerRepository.save(customerEntity).block();
		CustomerResponse response = CustomerMapper.mapperToResponse(savedEntity);
		logger.info("Customer created successfully: {}", response);
		return response;
	}

	/**
	 * Checks if the document number is already registered. This method queries the
	 * customer repository to verify if the document number exists and is active. If
	 * it is found, a warning is logged, and an IllegalArgumentException is thrown.
	 *
	 * @param documentNumber the document number to check for existence
	 * @throws IllegalArgumentException if the document number is already registered
	 */
	private void existsDocumentNumber(String documentNumber) {
		Optional.ofNullable(
				customerRepository.existsByDocumentNumberAndIsActiveTrue(documentNumber).block())
				.filter(Boolean::booleanValue).ifPresent(exists -> {
					logger.warn("The document is already registered: {}", documentNumber);
					throw new IllegalArgumentException("The document is already registered");
				});
	}

	/**
	 * Checks if the phone number is already registered. This method queries the
	 * customer repository to verify if the phone number exists and is active. If it
	 * is found, a warning is logged, and an IllegalArgumentException is thrown.
	 *
	 * @param phoneNumber the phone number to check for existence
	 * @throws IllegalArgumentException if the phone number is already registered
	 */
	private void existsPhoneNumber(String phoneNumber) {
		Optional.ofNullable(
				customerRepository.existsByPhoneNumberAndIsActiveTrue(phoneNumber).block())
				.filter(Boolean::booleanValue).ifPresent(exists -> {
					logger.warn("The phone is already registered: {}", phoneNumber);
					throw new IllegalArgumentException("The phone number is already registered");
				});

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

		CustomerEntity customerEntity = customerRepository
				.findByDocumentNumberAndIsActiveTrue(documentNumber)
				.switchIfEmpty(
						Mono.error(new IllegalArgumentException(
								"Customer not found with document: " + documentNumber)))
				.block();

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
		logger.info("All customers retrieved successfully");
		return customerRepository.findByIsActiveTrue().map(CustomerMapper::mapperToResponse)
				.collectList().toFuture()
				.join();
	}

	/**
	 * Updates a customer.
	 *
	 * @param documentNumber     The document number to update
	 * @param contactDataRequest The customer request payload containing update
	 *                           details
	 * @return The updated customer response
	 */
	@Override
	public CustomerResponse updateCustomer(String documentNumber,
			ContactDataRequest contactDataRequest) {
		logger.debug("Updating customer with document number: {}", documentNumber);
		existsPhoneNumber(contactDataRequest.getPhoneNumber());
		return customerRepository.findByDocumentNumberAndIsActiveTrue(documentNumber)
				.switchIfEmpty(Mono.error(new IllegalArgumentException("Customer not found")))
				.map(customerEntity -> {
					customerEntity.setPhoneNumber(contactDataRequest.getPhoneNumber());
					customerEntity.setAddress(contactDataRequest.getAddress());
					customerEntity.setEmail(contactDataRequest.getEmail());
					customerEntity.setUpdateDate(LocalDateTime.now());
					return customerEntity;
				}).flatMap(customerRepository::save).map(CustomerMapper::mapperToResponse)
				.doOnSuccess(response -> logger.info("Customer updated successfully: {}", response))
				.block();
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
				.switchIfEmpty(Mono.error(new IllegalArgumentException("Customer not found")))
				.map(customerEntity -> {
					customerEntity.setIsActive(false);
					customerEntity.setDeleteDate(LocalDateTime.now());
					return customerEntity;
				}).flatMap(customerRepository::save).doOnSuccess(customerEntity -> logger
						.info("Customer deleted successfully with document number: {}",
								documentNumber))
				.block();
	}
}
