package com.nttdata.bank.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.nttdata.bank.entity.CreditEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * CreditRepository provides CRUD operations for CreditEntity. This interface
 * extends ReactiveMongoRepository and defines custom query methods to check for
 * active credits by customer ID, find active credits by credit ID, and retrieve
 * all active credits.
 */
public interface CreditRepository extends ReactiveMongoRepository<CreditEntity, String> {

	/**
	 * Finds an active credit by document number.
	 *
	 * @param documentNumber The document number to search for.
	 * @return A Mono emitting the active CreditEntity object.
	 */
	Mono<CreditEntity> findByDocumentNumberAndIsActiveTrue(String documentNumber);

	/**
	 * Checks if an active credit exists by document number.
	 *
	 * @param documentNumber The document number to check for.
	 * @return A Mono emitting true if an active credit exists, false otherwise.
	 */
	Mono<Boolean> existsByDocumentNumberAndIsActiveTrue(String documentNumber);

	/**
	 * Finds all active credits.
	 *
	 * @return A Flux emitting active CreditEntity objects.
	 */
	Flux<CreditEntity> findByIsActiveTrue();

	/**
	 * Checks if an active credit exists by credit ID.
	 *
	 * @param creditId The credit ID to check for.
	 * @return A Mono emitting true if the active credit exists, false otherwise.
	 */
	Mono<Boolean> existsByIdAndIsActiveTrue(String creditId);

	/**
	 * Finds an active credit by its credit ID.
	 *
	 * @param creditId The credit ID to search for.
	 * @return A Mono emitting the active CreditEntity object.
	 */
	Mono<CreditEntity> findByIdAndIsActiveTrue(String creditId);

	/**
	 * Finds all active credits by document number.
	 *
	 * @param documentNumber The document number to search for.
	 * @return A Flux emitting active CreditEntity objects.
	 */
	Flux<CreditEntity> findAllByDocumentNumberAndIsActiveTrue(String documentNumber);
}
