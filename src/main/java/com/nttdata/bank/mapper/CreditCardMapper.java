package com.nttdata.bank.mapper;

import com.nttdata.bank.entity.CreditCardEntity;
import com.nttdata.bank.request.CreditCardRequest;
import com.nttdata.bank.response.CreditCardResponse;

/**
 * CreditCardMapper provides mapping methods to convert between
 * CreditCardRequest, CreditCardEntity, and CreditCardResponse objects. This
 * class includes methods to map a CreditCardRequest to a CreditCardEntity and
 * to map a CreditCardEntity to a CreditCardResponse.
 */

public class CreditCardMapper {

	/**
	 * Maps a CreditCardRequest object to a CreditCardEntity object.
	 *
	 * @param creditCardRequest The credit card request to map
	 * @return The mapped credit card entity
	 */
	public static CreditCardEntity mapperToEntity(CreditCardRequest creditCardRequest) {
		CreditCardEntity creditCardEntity = new CreditCardEntity();

		if (creditCardRequest != null) {
			creditCardEntity.setDocumentNumber(creditCardRequest.getDocumentNumber());
			creditCardEntity.setAvailableCredit(creditCardRequest.getAvailableCredit());
			creditCardEntity.setAnnualInterestRate(creditCardRequest.getAnnualInterestRate());
			creditCardEntity.setAnnualLateInterestRate(creditCardRequest.getAnnualLateInterestRate());
			creditCardEntity.setPaymentDay(creditCardRequest.getPaymentDay());
		}

		return creditCardEntity;
	}

	/**
	 * Maps a CreditCardEntity object to a CreditCardResponse object.
	 *
	 * @param creditCardEntity The credit card entity to map
	 * @return The mapped credit card response
	 */
	public static CreditCardResponse mapperToResponse(CreditCardEntity creditCardEntity) {
		CreditCardResponse creditCardResponse = new CreditCardResponse();

		if (creditCardEntity != null) {
			creditCardResponse.setAvailableCredit(creditCardEntity.getAvailableCredit());
			creditCardResponse.setAnnualInterestRate(creditCardEntity.getAnnualInterestRate());
			creditCardResponse.setAnnualLateInterestRate(creditCardEntity.getAnnualLateInterestRate());
			creditCardResponse.setPaymentDay(creditCardEntity.getPaymentDay());
			creditCardResponse.setCreditCardNumber(creditCardEntity.getCreditCardNumber());
			creditCardResponse.setAllowConsumption(creditCardEntity.getAllowConsumption());
		}

		return creditCardResponse;
	}
}
