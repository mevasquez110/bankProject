package com.nttdata.bank.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.nttdata.bank.entity.CreditEntity;

public interface CreditRepository extends MongoRepository<CreditEntity, String> {

    boolean existsActiveByCustomerId(String customerId);

	Optional<CreditEntity> findActiveById(String creditId);

	List<CreditEntity> findAllActive();

}
