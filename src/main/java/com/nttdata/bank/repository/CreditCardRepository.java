package com.nttdata.bank.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.nttdata.bank.entity.CreditCardEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * CreditCardRepository provides CRUD operations for CreditCardEntity. This
 * interface extends ReactiveMongoRepository and defines custom query methods to
 * find active credit cards by customer ID and retrieve all active credit cards.
 */
public interface CreditCardRepository extends ReactiveMongoRepository<CreditCardEntity, String> {

	/**
	 * Checks if an active credit card exists by document number.
	 *
	 * @param documentNumber The document number to check for.
	 * @return A Mono emitting true if an active credit card exists, false
	 *         otherwise.
	 */
	Mono<Boolean> existsByDocumentNumberAndIsActiveTrue(String documentNumber);

	/**
	 * Finds an active credit card by credit card number.
	 *
	 * @param creditCardNumber The credit card number to search for.
	 * @return A Mono emitting the active CreditCardEntity object.
	 */
	Mono<CreditCardEntity> findByCreditCardNumberAndIsActiveTrue(String creditCardNumber);

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

	/**
	 * Checks if a credit card exists by its credit card number.
	 *
	 * @param creditCardNumber The credit card number to search for.
	 * @return A Mono emitting a boolean indicating if the credit card exists.
	 */
	Mono<Boolean> existsByCreditCardNumber(String creditCardNumber);

	/**
	 * Finds all active credit cards by document number.
	 *
	 * @param documentNumber The document number to search for.
	 * @return A Flux emitting active CreditCardEntity objects.
	 */
	Flux<CreditCardEntity> findAllByDocumentNumberAndIsActiveTrue(String documentNumber);

	/**
	 * Checks if a credit card number exists and is active.
	 *
	 * @param creditCardNumber the credit card number to be checked
	 * @return a Mono that emits true if the credit card number exists and is
	 *         active, otherwise false
	 */
	Mono<Boolean> existsCreditCardNumberAndIsActiveTrue(String creditCardNumber);

}
