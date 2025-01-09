package com.nttdata.bank.repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.nttdata.bank.entity.CreditCardEntity;

public interface CreditCardRepository extends MongoRepository<CreditCardEntity, String> {

    Optional<CreditCardEntity> findActiveByCustomerId(String customerId);

	Optional<CreditCardEntity> findAllActive();
}
