package com.nttdata.bank.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.nttdata.bank.entity.CreditEntity;

/**
 * CreditRepository provides the CRUD operations for CreditEntity. This
 * interface extends MongoRepository and defines custom query methods to check
 * for active credits by customer ID, find active credits by credit ID, and
 * retrieve all active credits.
 */

public interface CreditRepository extends MongoRepository<CreditEntity, String> {

	/**
	 * Checks if an active credit exists by customer ID.
	 *
	 * @param customerId The customer ID to check for
	 * @return True if an active credit exists, false otherwise
	 */
	Boolean existsByCustomerIdAndIsActiveTrue(String customerId);

	/**
	 * Finds an active credit by credit ID.
	 *
	 * @param creditId The credit ID to search for
	 * @return An Optional containing the active CreditEntity object
	 */
	Optional<CreditEntity> findByCreditIdAndIsActiveTrue(String creditId);

	/**
	 * Finds all active credits.
	 *
	 * @return A list of active CreditEntity objects
	 */
	List<CreditEntity> findByIsActiveTrue();
}
