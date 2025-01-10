package com.nttdata.bank.repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.nttdata.bank.entity.AccountEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * AccountRepository provides the CRUD operations for AccountEntity. This
 * interface extends ReactiveMongoRepository and defines custom query methods to
 * find active accounts by customer ID, account number, and account ID.
 */
public interface AccountRepository extends ReactiveMongoRepository<AccountEntity, String> {

	/**
	 * Finds active accounts by document number.
	 *
	 * @param documentNumber The document number to search for.
	 * @return A Flux emitting active AccountEntity objects.
	 */
	Flux<AccountEntity> findByDocumentNumberAndIsActiveTrue(String documentNumber);

	/**
	 * Checks if an account exists by its account number.
	 *
	 * @param accountNumber The account number to check.
	 * @return A Mono emitting true if the account exists and is active, false
	 *         otherwise.
	 */
	Mono<Boolean> existsByAccountNumberAndIsActiveTrue(String accountNumber);

	/**
	 * Finds an active account by its account number.
	 *
	 * @param accountNumber The account number to search for.
	 * @return A Mono emitting the active AccountEntity object.
	 */
	Mono<AccountEntity> findByAccountNumberAndIsActiveTrue(String accountNumber);

	/**
	 * Finds all active accounts.
	 *
	 * @return A Flux emitting active AccountEntity objects.
	 */
	Flux<AccountEntity> findByIsActiveTrue();

	/**
	 * Finds an active account by its ID.
	 *
	 * @param accountId The account ID to search for.
	 * @return A Mono emitting the active AccountEntity object.
	 */
	Mono<Optional<AccountEntity>> findByIdAndIsActiveTrue(String accountId);
}
