package com.nttdata.bank.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.nttdata.bank.entity.CustomerEntity;

@Repository
public interface CustomerRepository extends MongoRepository<CustomerEntity, String> {

	Boolean existsByDocumentNumberAndIsActiveTrue(String documentNumber);

	CustomerEntity findByDocumentNumberAndIsActiveTrue(String documentNumber);

	List<CustomerEntity> findByIsActiveTrue();

	Optional<CustomerEntity> findByIdAndIsActiveTrue(String customerId);
}
