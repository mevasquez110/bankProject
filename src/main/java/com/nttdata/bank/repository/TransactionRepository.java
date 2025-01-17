package com.nttdata.bank.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.nttdata.bank.entity.TransactionEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * TransactionRepository provides the CRUD operations for TransactionEntity.
 * This interface extends ReactiveMongoRepository and defines custom query
 * methods to find active transactions by account number.
 */
public interface TransactionRepository extends ReactiveMongoRepository<TransactionEntity, String> {

	/**
	 * Finds active transactions by account number.
	 *
	 * @param accountNumber The account number to search for.
	 * @return A Flux emitting active TransactionEntity objects.
	 */
	Flux<TransactionEntity> findByAccountNumberAndIsActiveTrue(String accountNumber);

	Mono<Boolean> existsByOperationNumber(String operationNumber);
	
	Mono<TransactionEntity> findFirstByOrderByOperationNumberDesc();
}
