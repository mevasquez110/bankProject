package com.nttdata.bank.repository;

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
	 * Finds an active account that contains the given document number in the list
	 * of holder documents.
	 *
	 * @param holderDoc The document number to search for.
	 * @return A Flux emitting the found AccountEntity object.
	 */
	Flux<AccountEntity> findByHolderDocContainingAndIsActiveTrue(String holderDoc);

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

	Mono<Boolean> existsByAccountNumberAndDocumentNumberAndIsActiveTrue(String documentNumber, String primaryAccount);

	Mono<Boolean> existsByAccountNumberAndAmountGreaterThanEqual(String accountNumber, Double amount);

	Mono<Boolean> existsByAccountNumber(String accountNumber);
}
