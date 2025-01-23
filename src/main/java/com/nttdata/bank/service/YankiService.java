package com.nttdata.bank.service;

import java.util.List;
import com.nttdata.bank.request.YankiRequest;
import com.nttdata.bank.request.YankiUpdateRequest;
import com.nttdata.bank.response.YankiResponse;

/**
 * YankiService is the interface that provides methods for handling
 * Yanki-related operations. This includes creating Yanki entities, finding all
 * Yanki entities, updating Yanki entities, and deleting Yanki entities.
 */
public interface YankiService {

	/**
	 * Creates a new Yanki entity based on the provided request.
	 *
	 * @param yankiRequest The Yanki request containing the details for creating the
	 *                     Yanki entity
	 * @return YankiResponse containing the details of the created Yanki entity
	 */
	YankiResponse createYanki(YankiRequest yankiRequest);

	/**
	 * Retrieves all Yanki entities.
	 *
	 * @return List of YankiResponse containing details of all Yanki entities
	 */
	List<YankiResponse> findAllYanki();

	/**
	 * Updates a Yanki entity based on the provided phone number and request.
	 *
	 * @param phoneNumber        The phone number of the Yanki entity to update
	 * @param yankiUpdateRequest The request containing details for updating the
	 *                           Yanki entity
	 * @return YankiResponse containing the updated Yanki entity details
	 */
	YankiResponse updateYanki(String phoneNumber, YankiUpdateRequest yankiUpdateRequest);

	/**
	 * Deletes a Yanki entity based on the provided phone number.
	 *
	 * @param phoneNumber The phone number of the Yanki entity to delete
	 */
	void deleteYanki(String phoneNumber);
}
