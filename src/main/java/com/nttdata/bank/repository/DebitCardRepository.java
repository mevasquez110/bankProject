package com.nttdata.bank.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.nttdata.bank.entity.DebitCardEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * DebitCardRepository provides CRUD operations for DebitCardEntity. This
 * interface extends ReactiveMongoRepository and defines custom query methods to
 * find active debit cards by customer ID and retrieve all active debit cards.
 */
public interface DebitCardRepository extends ReactiveMongoRepository<DebitCardEntity, String> {

	/**
	 * Checks if an active debit card exists by document number.
	 *
	 * @param documentNumber The document number to check for.
	 * @return A Mono emitting true if an active debit card exists, false otherwise.
	 */
	Mono<Boolean> existsByDocumentNumberAndIsActiveTrue(String documentNumber);

	/**
	 * Finds all active debit cards.
	 *
	 * @return A Flux emitting active DebitCardEntity objects.
	 */
	Flux<DebitCardEntity> findByIsActiveTrue();

	/**
	 * Finds an active debit card by debit card number.
	 *
	 * @param debitCardNumber The debit card number to search for.
	 * @return A Mono emitting the active DebitCardEntity object.
	 */
	Mono<DebitCardEntity> findByDebitCardNumberAndIsActiveTrue(String debitCardNumber);

	/**
	 * Checks if an active debit card exists by its primary account number.
	 *
	 * @param primaryAccount The primary account number to check for.
	 * @return A Mono emitting true if an active debit card exists, false otherwise.
	 */
	Mono<Boolean> existsByPrimaryAccountAndIsActiveTrue(String primaryAccount);

	/**
	 * Checks if a debit card exists by its debit card number.
	 *
	 * @param debitCardNumber The debit card number to search for.
	 * @return A Mono emitting a boolean indicating if the debit card exists.
	 */
	Mono<Boolean> existsByDebitCardNumber(String debitCardNumber);
}
