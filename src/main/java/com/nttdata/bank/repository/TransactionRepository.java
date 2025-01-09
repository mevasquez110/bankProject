package com.nttdata.bank.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.nttdata.bank.entity.TransactionEntity;

public interface TransactionRepository extends MongoRepository<TransactionEntity, String> {

	List<TransactionEntity> findByAccountNumberAndIsActiveTrue(String accountNumber);
}
