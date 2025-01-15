package com.nttdata.bank.mapper;

import com.nttdata.bank.entity.DebitCardEntity;
import com.nttdata.bank.request.DebitCardRequest;
import com.nttdata.bank.response.DebitCardResponse;

public class DebitCardMapper {

	public static DebitCardEntity mapperToEntity(DebitCardRequest debitCardRequest) {
		DebitCardEntity debitCardEntity = new DebitCardEntity();

		if (debitCardRequest != null) {
			debitCardEntity.setDocumentNumber(debitCardRequest.getDocumentNumber());
			debitCardEntity.setPrimaryAccount(debitCardRequest.getPrimaryAccount());
		}

		return debitCardEntity;
	}

	public static DebitCardResponse mapperToResponse(DebitCardEntity debitCardEntity) {
		DebitCardResponse debitCardResponse = new DebitCardResponse();

		if (debitCardEntity != null) {
			debitCardResponse.setAssociatedAccounts(debitCardEntity.getAssociatedAccounts());
			debitCardResponse.setDebitCardNumber(debitCardEntity.getDebitCardNumber());
			debitCardResponse.setIsActive(debitCardEntity.getIsActive());
			debitCardResponse.setIsBlocked(debitCardEntity.getIsBlocked());
			debitCardResponse.setPrimaryAccount(debitCardEntity.getPrimaryAccount());
		}

		return debitCardResponse;
	}
}
