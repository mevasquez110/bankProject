package com.nttdata.bank.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.nttdata.bank.entity.YankiEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * YankiRepository provides CRUD operations for YankiEntity. This interface
 * extends ReactiveMongoRepository and defines custom query methods to check for
 * active Yanki entities by document number, account number, and phone number,
 * and retrieve all active Yanki entities.
 */
public interface YankiRepository extends ReactiveMongoRepository<YankiEntity, String> {

	/**
	 * Checks if an active Yanki entity exists by document number.
	 *
	 * @param documentNumber The document number to check for.
	 * @return A Mono emitting true if an active Yanki entity exists, false
	 *         otherwise.
	 */
	Mono<Boolean> existsByDocumentNumberAndIsActiveTrue(String documentNumber);

	/**
	 * Checks if an active Yanki entity exists by account number.
	 *
	 * @param accountNumber The account number to check for.
	 * @return A Mono emitting true if an active Yanki entity exists, false
	 *         otherwise.
	 */
	Mono<Boolean> existsByAccountNumberAndIsActiveTrue(String accountNumber);

	/**
	 * Finds an active Yanki entity by phone number.
	 *
	 * @param phoneNumber The phone number to search for.
	 * @return A Mono emitting the active YankiEntity object, if found.
	 */
	Mono<YankiEntity> findByPhoneNumberAndIsActiveTrue(String phoneNumber);

	/**
	 * Finds all active Yanki entities.
	 *
	 * @return A Flux emitting all active YankiEntity objects.
	 */
	Flux<YankiEntity> findAllByIsActiveTrue();
}
