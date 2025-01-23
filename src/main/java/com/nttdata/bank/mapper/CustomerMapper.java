package com.nttdata.bank.mapper;

import com.nttdata.bank.entity.CustomerEntity;
import com.nttdata.bank.request.CustomerRequest;
import com.nttdata.bank.response.CustomerResponse;

/**
 * CustomerMapper provides methods to convert between CustomerRequest,
 * CustomerEntity, and CustomerResponse objects. This class includes methods to
 * map a CustomerRequest to a CustomerEntity and to map a CustomerEntity to a
 * CustomerResponse.
 */

public class CustomerMapper {

	/**
	 * Maps a CustomerRequest object to a CustomerEntity object.
	 *
	 * @param customerRequest The customer request to map
	 * @return The mapped customer entity
	 */
	public static CustomerEntity mapperToEntity(CustomerRequest customerRequest) {
		CustomerEntity customerEntity = new CustomerEntity();

		if (customerRequest != null) {
			customerEntity.setFullName(customerRequest.getFullName());
			customerEntity.setCompanyName(customerRequest.getCompanyName());
			customerEntity.setEmail(customerRequest.getEmail());
			customerEntity.setAddress(customerRequest.getAddress());
			customerEntity.setPhoneNumber(customerRequest.getPhoneNumber());
			customerEntity.setDocumentType(customerRequest.getDocumentType());
			customerEntity.setDocumentNumber(customerRequest.getDocumentNumber());
			customerEntity.setPersonType(customerRequest.getPersonType());
		}

		return customerEntity;
	}

	/**
	 * Maps a CustomerEntity object to a CustomerResponse object.
	 *
	 * @param customerEntity The customer entity to map
	 * @return The mapped customer response
	 */
	public static CustomerResponse mapperToResponse(CustomerEntity customerEntity) {
		CustomerResponse customerResponse = new CustomerResponse();

		if (customerEntity != null) {
			customerResponse.setCustomerId(customerEntity.getId());
			customerResponse.setFullName(customerEntity.getFullName());
			customerResponse.setCompanyName(customerEntity.getCompanyName());
			customerResponse.setEmail(customerEntity.getEmail());
			customerResponse.setAddress(customerEntity.getAddress());
			customerResponse.setPhoneNumber(customerEntity.getPhoneNumber());
			customerResponse.setDocumentNumber(customerEntity.getDocumentNumber());
			customerResponse.setPersonType(customerEntity.getPersonType());
		}

		return customerResponse;
	}

}
