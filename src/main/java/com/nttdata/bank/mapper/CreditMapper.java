package com.nttdata.bank.mapper;

import com.nttdata.bank.entity.CreditEntity;
import com.nttdata.bank.request.CreditRequest;
import com.nttdata.bank.response.CreditResponse;
import com.nttdata.bank.util.Utility;

public class CreditMapper {

    /**
     * Maps a CreditRequest object to a CreditEntity object.
     *
     * @param creditRequest The credit request to map
     * @return The mapped credit entity
     */
    public static CreditEntity mapperToEntity(CreditRequest creditRequest) {
        CreditEntity creditEntity = new CreditEntity();

        if (creditRequest != null) {
            creditEntity.setCustomerId(creditRequest.getCustomerId());
            creditEntity.setAmount(creditRequest.getAmount());
            creditEntity.setAccountNumber(creditRequest.getAccountNumber());
            creditEntity.setAnnualInterestRate(creditRequest.getAnnualInterestRate());
            creditEntity.setNumberOfInstallments(creditRequest.getNumberOfInstallments());
            creditEntity.setPaymentDay(creditRequest.getPaymentDay());
            creditEntity.setAnnualLateInterestRate(creditRequest.getAnnualLateInterestRate());
        }

        return creditEntity;
    }

    /**
     * Maps a CreditEntity object to a CreditResponse object.
     *
     * @param creditEntity The credit entity to map
     * @return The mapped credit response
     */
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
