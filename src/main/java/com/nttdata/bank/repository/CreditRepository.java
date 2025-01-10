package com.nttdata.bank.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.nttdata.bank.entity.CreditEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * CreditRepository provides the CRUD operations for CreditEntity. This
 * interface extends ReactiveMongoRepository and defines custom query methods to
 * check for active credits by customer ID, find active credits by credit ID,
 * and retrieve all active credits.
 */
public interface CreditRepository extends ReactiveMongoRepository<CreditEntity, String> {

	/**
	 * Checks if an active credit exists by customer ID.
	 *
	 * @param documentNumber The document number to check for.
	 * @return A Mono emitting true if an active credit exists, false otherwise.
	 */
	Mono<Boolean> existsByDocumentNumberAndIsActiveTrue(String documentNumber);

	/**
	 * Finds an active credit by credit ID.
	 *
	 * @param creditId The credit ID to search for.
	 * @return A Mono emitting the active CreditEntity object.
	 */
	Mono<CreditEntity> findByCreditIdAndIsActiveTrue(String creditId);

	/**
	 * Finds all active credits.
	 *
	 * @return A Flux emitting active CreditEntity objects.
	 */
	Flux<CreditEntity> findByIsActiveTrue();
}
