package com.nttdata.bank.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.nttdata.bank.entity.AccountEntity;

/**
 * AccountRepository provides the CRUD operations for AccountEntity. This
 * interface extends MongoRepository and defines custom query methods to find
 * active accounts by customer ID, account number, and account ID.
 */

public interface AccountRepository extends MongoRepository<AccountEntity, String> {

	/**
	 * Finds active accounts by customer ID.
	 *
	 * @param customerId The customer ID to search for
	 * @return A list of active AccountEntity objects
	 */
	List<AccountEntity> findByCustomerIdAndIsActiveTrue(String customerId);

	/**
	 * Checks if an account exists by its account number.
	 *
	 * @param accountNumber The account number to check
	 * @return True if the account exists and is active, false otherwise
	 */
	Boolean existsByAccountNumberAndIsActiveTrue(String accountNumber);

	/**
	 * Finds an active account by its account number.
	 *
	 * @param accountNumber The account number to search for
	 * @return The active AccountEntity object
	 */
	AccountEntity findByAccountNumberAndIsActiveTrue(String accountNumber);

	/**
	 * Finds all active accounts.
	 *
	 * @return A list of active AccountEntity objects
	 */
	List<AccountEntity> findByIsActiveTrue();

	/**
	 * Finds an active account by its ID.
	 *
	 * @param accountId The account ID to search for
	 * @return An Optional containing the active AccountEntity object
	 */
	Optional<AccountEntity> findByIdAndIsActiveTrue(String accountId);
}
