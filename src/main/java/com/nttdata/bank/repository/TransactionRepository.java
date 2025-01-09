package com.nttdata.bank.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.nttdata.bank.entity.TransactionEntity;

/**
 * TransactionRepository provides the CRUD operations for TransactionEntity.
 * This interface extends MongoRepository and defines custom query methods to
 * find active transactions by account number.
 */

public interface TransactionRepository extends MongoRepository<TransactionEntity, String> {

	/**
	 * Finds active transactions by account number.
	 *
	 * @param accountNumber The account number to search for
	 * @return A list of active TransactionEntity objects
	 */
	List<TransactionEntity> findByAccountNumberAndIsActiveTrue(String accountNumber);
}
