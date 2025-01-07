package com.nttdata.bank.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.nttdata.bank.entity.AccountEntity;

public interface AccountRepository extends MongoRepository<AccountEntity, String> {

	List<AccountEntity> findByCustomerId(String customerId);

	boolean existsByAccountNumber(String accountNumber);

	AccountEntity findByAccountNumber(String accountNumber);
}
