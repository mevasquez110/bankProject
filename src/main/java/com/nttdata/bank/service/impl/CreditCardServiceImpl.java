package com.nttdata.bank.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nttdata.bank.entity.CreditCardEntity;
import com.nttdata.bank.entity.PaymentScheduleEntity;
import com.nttdata.bank.mapper.CreditCardMapper;
import com.nttdata.bank.repository.CreditCardRepository;
import com.nttdata.bank.repository.PaymentScheduleRepository;
import com.nttdata.bank.request.CreditCardRequest;
import com.nttdata.bank.response.CreditCardDebtResponse;
import com.nttdata.bank.response.CreditCardResponse;
import com.nttdata.bank.service.CreditCardService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CreditCardServiceImpl implements CreditCardService {

	@Autowired
	private CreditCardRepository creditCardRepository;

	@Autowired
	private PaymentScheduleRepository paymentScheduleRepository;

	@Override
	public CreditCardResponse requestCreditCard(CreditCardRequest creditCardRequest) {
		Optional<CreditCardEntity> existingCard = creditCardRepository
				.findByCustomerId(creditCardRequest.getCustomerId());

		if (existingCard.isPresent()) {
			throw new RuntimeException("El cliente ya tiene una tarjeta de crédito");
		}

		CreditCardEntity creditCardEntity = CreditCardMapper.mapperToEntity(creditCardRequest);
		CreditCardEntity savedCard = creditCardRepository.save(creditCardEntity);
		return CreditCardMapper.mapperToResponse(savedCard);
	}

	@Override
	public CreditCardDebtResponse checkDebt(String creditCardId) {
		CreditCardEntity creditCard = creditCardRepository.findById(creditCardId)
				.orElseThrow(() -> new RuntimeException("Tarjeta de crédito no encontrada"));

		List<PaymentScheduleEntity> paymentSchedules = paymentScheduleRepository.findByCreditCardNumber(creditCardId);

		LocalDate today = LocalDate.now();
		int currentMonth = today.getMonthValue();
		int currentYear = today.getYear();

		Double totalDebt = paymentSchedules.stream().filter(payment -> !payment.getPaid())
				.mapToDouble(PaymentScheduleEntity::getDebtAmount).sum();

		Double currentMonthShare = paymentSchedules.stream().filter(payment -> !payment.getPaid())
				.filter(payment -> payment.getPaymentDate().getMonthValue() == currentMonth
						&& payment.getPaymentDate().getYear() == currentYear)
				.mapToDouble(PaymentScheduleEntity::getSharePayment).sum();

		CreditCardDebtResponse response = new CreditCardDebtResponse();
		response.setCreditCardNumber(creditCard.getCreditCardId());
		response.setTotalDebt(totalDebt);
		response.setShare(currentMonthShare);
		response.setAvailableCredit(creditCard.getAvailableCredit());
		return response;
	}

	@Override
	public List<CreditCardResponse> findAllCreditCards() {
		return creditCardRepository.findAll().stream().map(CreditCardMapper::mapperToResponse)
				.collect(Collectors.toList());
	}

	@Override
	public CreditCardResponse updateCreditCard(String creditCardId, CreditCardRequest creditCardRequest) {
		CreditCardEntity creditCardEntity = creditCardRepository.findById(creditCardId)
				.orElseThrow(() -> new RuntimeException("Tarjeta de crédito no encontrada"));
		creditCardEntity = CreditCardMapper.mapperToEntity(creditCardRequest);
		creditCardEntity.setCreditCardId(creditCardId);
		creditCardEntity = creditCardRepository.save(creditCardEntity);
		return CreditCardMapper.mapperToResponse(creditCardEntity);
	}

	@Override
	public void deleteCreditCard(String creditCardId) {
		creditCardRepository.deleteById(creditCardId);
	}
}
