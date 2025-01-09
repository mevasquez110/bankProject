package com.nttdata.bank.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.nttdata.bank.entity.CustomerEntity;

/**
 * CustomerRepository provides the CRUD operations for CustomerEntity. This
 * interface extends MongoRepository and defines custom query methods to check
 * for active customers by document number, find active customers by document
 * number, retrieve all active customers, and find active customers by ID.
 */

@Repository
public interface CustomerRepository extends MongoRepository<CustomerEntity, String> {

	/**
	 * Checks if an active customer exists by document number.
	 *
	 * @param documentNumber The document number to check for
	 * @return True if an active customer exists, false otherwise
	 */
	Boolean existsByDocumentNumberAndIsActiveTrue(String documentNumber);

	/**
	 * Finds an active customer by document number.
	 *
	 * @param documentNumber The document number to search for
	 * @return The active CustomerEntity object
	 */
	CustomerEntity findByDocumentNumberAndIsActiveTrue(String documentNumber);

	/**
	 * Finds all active customers.
	 *
	 * @return A list of active CustomerEntity objects
	 */
	List<CustomerEntity> findByIsActiveTrue();

	/**
	 * Finds an active customer by ID.
	 *
	 * @param customerId The customer ID to search for
	 * @return An Optional containing the active CustomerEntity object
	 */
	Optional<CustomerEntity> findByIdAndIsActiveTrue(String customerId);
}
