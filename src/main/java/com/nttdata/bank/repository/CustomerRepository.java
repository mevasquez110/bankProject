package com.nttdata.bank.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.nttdata.bank.entity.CustomerEntity;

@Repository
public interface CustomerRepository extends MongoRepository<CustomerEntity, String> {

    Boolean existsActiveByDocumentNumber(String documentNumber);

    CustomerEntity findActiveByDocumentNumber(String documentNumber);

    List<CustomerEntity> findAllActive();

    Optional<CustomerEntity> findActiveById(String customerId);
}
