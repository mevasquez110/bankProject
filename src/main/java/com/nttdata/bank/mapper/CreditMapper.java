package com.nttdata.bank.mapper;

import java.time.LocalDateTime;
import com.nttdata.bank.entity.CreditEntity;
import com.nttdata.bank.request.CreditRequest;
import com.nttdata.bank.response.CreditResponse;
import com.nttdata.bank.util.Utility;

public class CreditMapper {

	public static CreditEntity mapperToEntity(CreditRequest creditRequest) {
		CreditEntity creditEntity = new CreditEntity();

		if (creditRequest != null) {
			creditEntity.setCustomerId(creditRequest.getCustomerId());
			creditEntity.setAmount(creditRequest.getAmount());
			creditEntity.setAccountNumber(creditRequest.getAccountNumber());
			creditEntity.setAnnualInterestRate(creditRequest.getAnnualInterestRate());
			creditEntity.setNumberOfInstallments(creditRequest.getNumberOfInstallments());
			creditEntity.setPaymentDay(creditRequest.getPaymentDay());
			creditEntity.setCreateDate(LocalDateTime.now());
			creditEntity.setAnnualLateInterestRate(creditRequest.getAnnualLateInterestRate());
		}

		return creditEntity;
	}

	public static CreditResponse mapperToResponse(CreditEntity creditEntity) {
		CreditResponse creditResponse = new CreditResponse();

		if (creditEntity != null) {
			creditResponse.setCreditId(creditEntity.getCreditId());
			creditResponse.setAmount(creditEntity.getAmount());
			creditResponse.setAnnualInterestRate(creditEntity.getAnnualInterestRate());
			creditResponse.setNumberOfInstallments(creditEntity.getNumberOfInstallments());
			creditResponse.setInstallmentAmount(Utility.calculateInstallmentAmount(creditEntity.getAmount(),
					creditEntity.getAnnualInterestRate(), creditEntity.getNumberOfInstallments()));
			creditResponse.setPaymentDay(creditEntity.getPaymentDay());
			creditResponse.setCreateDate(creditEntity.getCreateDate());
			creditResponse.setAnnualInterestRate(creditEntity.getAnnualLateInterestRate());
		}

		return creditResponse;
	}
}
