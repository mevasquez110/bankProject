package com.nttdata.bank.service.impl;

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

	@Autowired
	private CustomerRepository customerRepository;

	@Override
	public CustomerResponse createCustomer(CustomerRequest customerRequest) {
		if (customerRepository.existsActiveByDocumentNumber(customerRequest.getDocumentNumber())) {
			throw new IllegalArgumentException("The document is already registered");
		}

		CustomerEntity customerEntity = CustomerMapper.mapperToEntity(customerRequest);
		customerEntity.setIsActive(true);
		customerEntity.setCreateDate(LocalDateTime.now());
		customerEntity = customerRepository.save(customerEntity);
		return CustomerMapper.mapperToResponse(customerEntity);
	}

	@Override
	public CustomerResponse getCustomerByDocumentNumber(String documentNumber) {
		CustomerEntity customerEntity = customerRepository.findActiveByDocumentNumber(documentNumber);

		if (customerEntity == null) {
			throw new IllegalArgumentException("Customer not found with document number: " + documentNumber);
		}

		return CustomerMapper.mapperToResponse(customerEntity);
	}

	@Override
	public List<CustomerResponse> findAllCustomers() {
		return customerRepository.findAllActive().stream().map(CustomerMapper::mapperToResponse)
				.collect(Collectors.toList());
	}

	@Override
	public CustomerResponse updateCustomer(String customerId, CustomerRequest customerRequest) {
		CustomerEntity customerEntity = customerRepository.findActiveById(customerId)
				.orElseThrow(() -> new RuntimeException("Customer not found"));
		
		validateUpdateFields(customerRequest, customerEntity);
		customerEntity.setPhoneNumber(customerRequest.getPhoneNumber());
		customerEntity.setAddress(customerRequest.getAddress());
		customerEntity.setEmail(customerRequest.getEmail());
		customerEntity.setUpdateDate(LocalDateTime.now());
		customerEntity = customerRepository.save(customerEntity);
		return CustomerMapper.mapperToResponse(customerEntity);
	}

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

	@Override
	public void deleteCustomer(String customerId) {
		CustomerEntity customerEntity = customerRepository.findActiveById(customerId)
				.orElseThrow(() -> new RuntimeException("Customer not found"));
		
		customerEntity.setIsActive(false);
		customerEntity.setDeleteDate(LocalDateTime.now());
		customerEntity = customerRepository.save(customerEntity);
	}
}
