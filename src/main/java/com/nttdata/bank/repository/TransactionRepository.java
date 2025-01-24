package com.nttdata.bank.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.nttdata.bank.entity.TransactionEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * TransactionRepository provides CRUD operations for TransactionEntity. This
 * interface extends ReactiveMongoRepository and defines custom query methods to
 * find active transactions by account number.
 */
public interface TransactionRepository extends ReactiveMongoRepository<TransactionEntity, String> {

	/**
	 * Finds the most recent transaction by operation number.
	 *
	 * @return A Mono emitting the most recent TransactionEntity object.
	 */
	Mono<TransactionEntity> findFirstByOrderByOperationNumberDesc();

	/**
	 * Finds all active transactions involving.
	 * 
	 * @return A Flux emitting active TransactionEntity.
	 */
	Flux<TransactionEntity> findAllByIsActiveTrue();
}
