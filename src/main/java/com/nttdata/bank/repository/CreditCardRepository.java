package com.nttdata.bank.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.nttdata.bank.entity.CreditCardEntity;

/**
 * CreditCardRepository provides the CRUD operations for CreditCardEntity. This
 * interface extends MongoRepository and defines custom query methods to find
 * active credit cards by customer ID and retrieve all active credit cards.
 */

public interface CreditCardRepository extends MongoRepository<CreditCardEntity, String> {

	/**
	 * Finds an active credit card by customer ID.
	 *
	 * @param customerId The customer ID to search for
	 * @return An Optional containing the active CreditCardEntity object
	 */
	Optional<CreditCardEntity> findByCustomerIdAndIsActiveTrue(String customerId);

	/**
	 * Finds all active credit cards.
	 *
	 * @return A list of active CreditCardEntity objects
	 */
	List<CreditCardEntity> findByIsActiveTrue();
}
