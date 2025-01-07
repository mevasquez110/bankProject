package com.nttdata.bank.service.impl;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nttdata.bank.entity.CreditCardEntity;
import com.nttdata.bank.repository.CreditCardRepository;
import com.nttdata.bank.response.CreditCardDebtResponse;
import com.nttdata.bank.response.CreditCardResponse;
import com.nttdata.bank.service.CreditCardService;

@Service
public class CreditCardServiceImpl implements CreditCardService {

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Override
    public CreditCardResponse requestCreditCard(String customerId) {
        Optional<CreditCardEntity> existingCard = creditCardRepository.findByCustomerId(customerId);
       
        if (existingCard.isPresent()) {
            throw new RuntimeException("El cliente ya tiene una tarjeta de crédito");
        }

        CreditCardEntity newCard = new CreditCardEntity();
        newCard.setCustomerId(customerId);
        newCard.setAmount(0.0);
        newCard.setAvailableCredit(5000.0); 
        newCard.setAnnualInterestRate(15.0);
        newCard.setAnnualLateInterestRate(25.0);
        newCard.setPaymentDay(5); 
        newCard.setCreateDate(LocalDate.now());
        CreditCardEntity savedCard = creditCardRepository.save(newCard);

        CreditCardResponse response = new CreditCardResponse();
        response.setAmount(savedCard.getAmount());
        response.setAnnualInterestRate(savedCard.getAnnualInterestRate());
        response.setAnnualLateInterestRate(savedCard.getAnnualLateInterestRate());
        response.setPaymentDay(savedCard.getPaymentDay());
        return response;
    }

    @Override
    public CreditCardDebtResponse checkDebt(String creditCardId) {
        CreditCardEntity creditCard = creditCardRepository.findById(creditCardId)
                .orElseThrow(() -> new RuntimeException("Tarjeta de crédito no encontrada"));

        Double totalDebt = creditCard.getAmount() - creditCard.getAvailableCredit();
        Double share = creditCard.getAmount();

        CreditCardDebtResponse response = new CreditCardDebtResponse();
        response.setCreditCardNumber(creditCard.getCreditCardId());
        response.setTotalDebt(totalDebt);
        response.setShare(share);

        return response;
    }
}
