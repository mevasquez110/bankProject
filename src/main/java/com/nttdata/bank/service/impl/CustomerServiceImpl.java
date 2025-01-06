package com.nttdata.bank.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nttdata.bank.entity.CustomerEntity;
import com.nttdata.bank.mapper.CustomerMapper;
import com.nttdata.bank.repository.CustomerRepository;
import com.nttdata.bank.request.CustomerRequest;
import com.nttdata.bank.response.CustomerResponse;
import com.nttdata.bank.service.CustomerService;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private CustomerRepository customerRepository;

	public CustomerResponse registerCustomer(CustomerRequest customerRequest) {
		CustomerResponse customerResponse = new CustomerResponse();
		CustomerEntity customerEntity = CustomerMapper.mapperToEntity(customerRequest);
		customerEntity = customerRepository.save(customerEntity);
		
		if (customerEntity != null) {
			customerResponse = CustomerMapper.mapperToResponse(customerEntity);
		}
		
		return customerResponse;
	}

	public CustomerResponse getCustomerById(String customerId) {
		CustomerResponse customerResponse = new CustomerResponse();
		CustomerEntity customerEntity = customerRepository.findById(customerId).orElse(null);

		if (customerEntity != null) {
			customerResponse = CustomerMapper.mapperToResponse(customerEntity);
		}

		return customerResponse;
	}
}
