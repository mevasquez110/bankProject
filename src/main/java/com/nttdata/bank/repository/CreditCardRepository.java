package com.nttdata.bank.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.nttdata.bank.entity.CreditCardEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * CreditCardRepository provides the CRUD operations for CreditCardEntity. This
 * interface extends ReactiveMongoRepository and defines custom query methods to
 * find active credit cards by customer ID and retrieve all active credit cards.
 */

public interface CreditCardRepository extends ReactiveMongoRepository<CreditCardEntity, String> {

	/**
	 * Finds an active credit card by document number.
	 *
	 * @param documentNumber The document number to search for.
	 * @return A Mono emitting the active CreditCardEntity object.
	 */
	Mono<CreditCardEntity> findByDocumentNumberAndIsActiveTrue(String documentNumber);

	/**
	 * Finds all active credit cards.
	 *
	 * @return A Flux emitting active CreditCardEntity objects.
	 */
	Flux<CreditCardEntity> findByIsActiveTrue();
}
