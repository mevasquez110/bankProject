package com.nttdata.bank.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.nttdata.bank.entity.AccountEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * AccountRepository provides CRUD operations for AccountEntity. This interface
 * extends ReactiveMongoRepository and defines custom query methods to find
 * active accounts by customer ID, account number, and account ID.
 */
public interface AccountRepository extends ReactiveMongoRepository<AccountEntity, String> {

	/**
	 * Finds an active account that contains the given document number in the list
	 * of holder documents.
	 *
	 * @param holderDoc The document number to search for.
	 * @return A Flux emitting the found AccountEntity object.
	 */
	Flux<AccountEntity> findByHolderDocContainingAndIsActiveTrue(String holderDoc);

	/**
	 * Finds an active account by its account number.
	 *
	 * @param accountNumber The account number to search for.
	 * @return A Mono emitting the active AccountEntity object.
	 */
	Mono<AccountEntity> findByAccountNumberAndIsActiveTrue(String accountNumber);

	/**
	 * Checks if an active account exists by its account number and document number.
	 *
	 * @param holderDoc     The document number to search for.
	 * @param accountNumber The account number to check.
	 * @return A Mono emitting a boolean indicating if the account exists and is
	 *         active.
	 */
	Mono<Boolean> existsByAccountNumberAndHolderDocAndIsActiveTrue(String holderDoc, String accountNumber);

	/**
	 * Checks if an account has an amount greater than or equal to the specified
	 * value.
	 *
	 * @param accountNumber The account number to search for.
	 * @param amount        The minimum amount to check.
	 * @return A Mono emitting a boolean indicating if the account meets the
	 *         criteria.
	 */
	Mono<Boolean> existsByAccountNumberAndAmountGreaterThanEqual(String accountNumber, Double amount);

	/**
	 * Checks if an account exists by its account number.
	 *
	 * @param accountNumber The account number to search for.
	 * @return A Mono emitting a boolean indicating if the account exists.
	 */
	Mono<Boolean> existsByAccountNumber(String accountNumber);

	/**
	 * Retrieves all active accounts.
	 *
	 * @return A Flux emitting the list of active AccountEntity objects.
	 */
	Flux<AccountEntity> findAllByIsActiveTrue();
}
