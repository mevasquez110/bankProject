package com.nttdata.bank.mapper;

import com.nttdata.bank.entity.CreditCardEntity;
import com.nttdata.bank.request.CreditCardRequest;
import com.nttdata.bank.response.CreditCardResponse;

public class CreditCardMapper {

    public static CreditCardEntity mapperToEntity(CreditCardRequest creditCardRequest) {
        CreditCardEntity creditCardEntity = new CreditCardEntity();

        if (creditCardRequest != null) {
            creditCardEntity.setCustomerId(creditCardRequest.getCustomerId());
            creditCardEntity.setAvailableCredit(creditCardRequest.getAvailableCredit());
            creditCardEntity.setAnnualInterestRate(creditCardRequest.getAnnualInterestRate());
            creditCardEntity.setAnnualLateInterestRate(creditCardRequest.getAnnualLateInterestRate());
            creditCardEntity.setPaymentDay(creditCardRequest.getPaymentDay());
        }

        return creditCardEntity;
    }

    public static CreditCardResponse mapperToResponse(CreditCardEntity creditCardEntity) {
        CreditCardResponse creditCardResponse = new CreditCardResponse();

        if (creditCardEntity != null) {
            creditCardResponse.setAvailableCredit(creditCardEntity.getAvailableCredit());
            creditCardResponse.setAnnualInterestRate(creditCardEntity.getAnnualInterestRate());
            creditCardResponse.setAnnualLateInterestRate(creditCardEntity.getAnnualLateInterestRate());
            creditCardResponse.setPaymentDay(creditCardEntity.getPaymentDay());
            creditCardResponse.setCreditCardNumber(creditCardEntity.getCreditCardId());
            creditCardResponse.setCreateDate(creditCardEntity.getCreateDate());
        }

        return creditCardResponse;
    }
}
