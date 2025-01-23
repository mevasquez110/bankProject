package com.nttdata.bank.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import com.nttdata.bank.entity.CustomerEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * CustomerRepository provides CRUD operations for CustomerEntity. This
 * interface extends ReactiveMongoRepository and defines custom query methods to
 * check for active customers by document number, find active customers by
 * document number, retrieve all active customers, and find active customers by
 * ID.
 */

@Repository
public interface CustomerRepository extends ReactiveMongoRepository<CustomerEntity, String> {

	/**
	 * Checks if an active customer exists by document number.
	 *
	 * @param documentNumber The document number to check for.
	 * @return A Mono that emits true if an active customer exists, false otherwise.
	 */
	Mono<Boolean> existsByDocumentNumberAndIsActiveTrue(String documentNumber);

	/**
	 * Checks if an active customer exists by phone number.
	 *
	 * @param phoneNumber The phone number to check for.
	 * @return A Mono that emits true if an active customer exists, false otherwise.
	 */
	Mono<Boolean> existsByPhoneNumberAndIsActiveTrue(String phoneNumber);

	/**
	 * Finds an active customer by document number.
	 *
	 * @param documentNumber The document number to search for.
	 * @return A Mono emitting the active CustomerEntity object.
	 */
	Mono<CustomerEntity> findByDocumentNumberAndIsActiveTrue(String documentNumber);

	/**
	 * Finds all active customers.
	 *
	 * @return A Flux emitting a list of active CustomerEntity objects.
	 */
	Flux<CustomerEntity> findByIsActiveTrue();

}
