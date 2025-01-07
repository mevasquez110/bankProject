package com.nttdata.bank.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.nttdata.bank.entity.CreditEntity;

public interface CreditRepository extends MongoRepository<CreditEntity, String> {

	boolean existsByCustomerIdAndStatus(String customerId, String status);

}
