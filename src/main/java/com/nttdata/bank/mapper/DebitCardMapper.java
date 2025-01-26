package com.nttdata.bank.mapper;

import com.nttdata.bank.entity.DebitCardEntity;
import com.nttdata.bank.request.DebitCardRequest;
import com.nttdata.bank.response.DebitCardResponse;

/**
 * DebitCardMapper provides methods to convert between DebitCardRequest,
 * DebitCardEntity, and DebitCardResponse objects. This class includes methods
 * to map a DebitCardRequest to a DebitCardEntity and to map a DebitCardEntity
 * to a DebitCardResponse.
 */

public class DebitCardMapper {

	/**
	 * Maps a DebitCardRequest object to a DebitCardEntity object.
	 *
	 * @param debitCardRequest The debit card request to map
	 * @return The mapped debit card entity
	 */
	public static DebitCardEntity mapperToEntity(DebitCardRequest debitCardRequest) {
		DebitCardEntity debitCardEntity = new DebitCardEntity();
		debitCardEntity.setDocumentNumber(debitCardRequest.getDocumentNumber());
		debitCardEntity.setPrimaryAccount(debitCardRequest.getPrimaryAccount());
		return debitCardEntity;
	}

	/**
	 * Maps a DebitCardEntity object to a DebitCardResponse object.
	 *
	 * @param debitCardEntity The debit card entity to map
	 * @return The mapped debit card response
	 */
	public static DebitCardResponse mapperToResponse(DebitCardEntity debitCardEntity) {
		DebitCardResponse debitCardResponse = new DebitCardResponse();
		debitCardResponse.setDebitCardNumber(debitCardEntity.getDebitCardNumber());
		debitCardResponse.setAssociatedAccounts(debitCardEntity.getAssociatedAccounts());
		debitCardResponse.setPrimaryAccount(debitCardEntity.getPrimaryAccount());
		debitCardResponse.setIsBlocked(debitCardEntity.getIsBlocked());
		debitCardResponse.setIsActive(debitCardEntity.getIsActive());
		return debitCardResponse;
	}
}
