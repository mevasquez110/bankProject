package com.nttdata.bank.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.nttdata.bank.entity.CustomerEntity;

@Repository
public interface CustomerRepository extends MongoRepository<CustomerEntity, String> {

	boolean existsByDocumentNumber(String documentNumber);

	CustomerEntity findByDocumentNumber(String documentNumber);
}