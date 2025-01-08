package com.nttdata.bank.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nttdata.bank.entity.CreditCardEntity;
import com.nttdata.bank.entity.PaymentScheduleEntity;
import com.nttdata.bank.repository.CreditCardRepository;
import com.nttdata.bank.repository.PaymentScheduleRepository;
import com.nttdata.bank.request.CreditCardRequest;
import com.nttdata.bank.response.CreditCardDebtResponse;
import com.nttdata.bank.response.CreditCardResponse;
import com.nttdata.bank.service.CreditCardService;

@Service
public class CreditCardServiceImpl implements CreditCardService {

    @Autowired
    private CreditCardRepository creditCardRepository;

	@Autowired
	private PaymentScheduleRepository paymentScheduleRepository;
	
    @Override
    public CreditCardResponse requestCreditCard(CreditCardRequest creditCardRequest) {
        Optional<CreditCardEntity> existingCard = creditCardRepository.findByCustomerId(creditCardRequest.getCustomerId());
       
        if (existingCard.isPresent()) {
            throw new RuntimeException("El cliente ya tiene una tarjeta de crédito");
        }

        CreditCardEntity creditCardEntity = new CreditCardEntity();
        creditCardEntity.setCustomerId(creditCardRequest.getCustomerId());
        creditCardEntity.setAvailableCredit(creditCardRequest.getAvailableCredit());
        creditCardEntity.setAnnualInterestRate(creditCardRequest.getAnnualInterestRate());
        creditCardEntity.setAnnualLateInterestRate(creditCardRequest.getAnnualLateInterestRate());
        creditCardEntity.setPaymentDay(creditCardRequest.getPaymentDay()); 
        creditCardEntity.setCreateDate(LocalDate.now());
        CreditCardEntity savedCard = creditCardRepository.save(creditCardEntity);

        CreditCardResponse creditCardResponse = new CreditCardResponse();
        creditCardResponse.setAvailableCredit(savedCard.getAvailableCredit());
        creditCardResponse.setAnnualInterestRate(savedCard.getAnnualInterestRate());
        creditCardResponse.setAnnualLateInterestRate(savedCard.getAnnualLateInterestRate());
        creditCardResponse.setPaymentDay(savedCard.getPaymentDay());
        return creditCardResponse;
    }

    @Override
    public CreditCardDebtResponse checkDebt(String creditCardId) {
        CreditCardEntity creditCard = creditCardRepository.findById(creditCardId)
                .orElseThrow(() -> new RuntimeException("Tarjeta de crédito no encontrada"));

        List<PaymentScheduleEntity> paymentSchedules = paymentScheduleRepository.findByCreditCardNumber(creditCardId);

        LocalDate today = LocalDate.now();
        int currentMonth = today.getMonthValue();
        int currentYear = today.getYear();

        Double totalDebt = paymentSchedules.stream()
                .filter(payment -> !payment.getPaid())
                .mapToDouble(PaymentScheduleEntity::getDebtAmount)
                .sum();

        Double currentMonthShare = paymentSchedules.stream()
                .filter(payment -> !payment.getPaid())
                .filter(payment -> payment.getPaymentDate().getMonthValue() == currentMonth && payment.getPaymentDate().getYear() == currentYear)
                .mapToDouble(PaymentScheduleEntity::getSharePayment)
                .sum();

        CreditCardDebtResponse response = new CreditCardDebtResponse();
        response.setCreditCardNumber(creditCard.getCreditCardId());
        response.setTotalDebt(totalDebt);
        response.setShare(currentMonthShare);
        response.setAvailableCredit(creditCard.getAvailableCredit());
        return response;
    }

}
