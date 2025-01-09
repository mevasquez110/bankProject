package com.nttdata.bank.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.nttdata.bank.entity.AccountEntity;

public interface AccountRepository extends MongoRepository<AccountEntity, String> {

	List<AccountEntity> findActiveByCustomerId(String customerId);

	Boolean existsActiveByAccountNumber(String accountNumber);

	AccountEntity findActiveByAccountNumber(String accountNumber);

	List<AccountEntity> findAllActive();

	Optional<AccountEntity> findActiveById(String accountId);
}
