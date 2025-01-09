package com.nttdata.bank.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * * CreditCardServiceImpl is the implementation class for the CreditCardService
 * interface. * This class provides the actual logic for handling credit
 * card-related operations such as requesting a credit card, * checking credit
 * card debt, finding all credit cards, updating a credit card, and deleting a
 * credit card.
 */

@Service
public class CreditCardServiceImpl implements CreditCardService {

	private static final Logger logger = LoggerFactory.getLogger(CreditCardServiceImpl.class);

	@Autowired
	private CreditCardRepository creditCardRepository;

	@Autowired
	private PaymentScheduleRepository paymentScheduleRepository;

	/**
	 * Requests a new credit card.
	 *
	 * @param creditCardRequest The credit card request payload
	 * @return The credit card response
	 */
	@Override
	public CreditCardResponse requestCreditCard(CreditCardRequest creditCardRequest) {
		logger.debug("Requesting credit card: {}", creditCardRequest);
		Optional<CreditCardEntity> existingCard = creditCardRepository
				.findByCustomerIdAndIsActiveTrue(creditCardRequest.getCustomerId());

		if (existingCard.isPresent()) {
			logger.warn("The customer already has a credit card: {}", creditCardRequest.getCustomerId());
			throw new RuntimeException("El cliente ya tiene una tarjeta de crédito");
		}

		CreditCardEntity creditCardEntity = CreditCardMapper.mapperToEntity(creditCardRequest);
		creditCardEntity.setCreateDate(LocalDateTime.now());
		creditCardEntity.setIsActive(true);
		CreditCardEntity savedCard = creditCardRepository.save(creditCardEntity);
		CreditCardResponse response = CreditCardMapper.mapperToResponse(savedCard);
		logger.info("Credit card created successfully: {}", response);
		return response;
	}

	/**
	 * Checks the debt of a credit card.
	 *
	 * @param creditCardId The credit card ID
	 * @return The credit card debt response
	 */
	@Override
	public CreditCardDebtResponse checkDebt(String creditCardId) {
		logger.debug("Checking debt for credit card: {}", creditCardId);
		CreditCardEntity creditCard = creditCardRepository.findById(creditCardId).orElseThrow(() -> {
			logger.error("Credit card not found: {}", creditCardId);
			return new RuntimeException("Tarjeta de crédito no encontrada");
		});

		List<PaymentScheduleEntity> paymentSchedules = paymentScheduleRepository.findByCreditCardNumber(creditCardId);

		LocalDateTime today = LocalDateTime.now();
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
		logger.info("Debt checked successfully for credit card: {}", creditCardId);
		return response;
	}

	/**
	 * Finds all credit cards.
	 *
	 * @return A list of credit card responses
	 */
	@Override
	public List<CreditCardResponse> findAllCreditCards() {
		logger.debug("Finding all credit cards");
		List<CreditCardResponse> creditCards = creditCardRepository.findByIsActiveTrue().stream()
				.map(CreditCardMapper::mapperToResponse).collect(Collectors.toList());
		logger.info("All credit cards retrieved successfully");
		return creditCards;
	}

	/**
	 * Updates a credit card.
	 *
	 * @param creditCardId The credit card ID
	 * @return The updated credit card response
	 */
	@Override
	public CreditCardResponse updateCreditCard(String creditCardId) {
		logger.debug("Updating credit card with ID: {}", creditCardId);
		CreditCardEntity creditCardEntity = creditCardRepository.findById(creditCardId).orElseThrow(() -> {
			logger.error("Credit card not found: {}", creditCardId);
			return new RuntimeException("Tarjeta de crédito no encontrada");
		});

		creditCardEntity.setAllowConsumption(!creditCardEntity.getAllowConsumption());
		creditCardEntity.setUpdateDate(LocalDateTime.now());
		creditCardEntity = creditCardRepository.save(creditCardEntity);
		CreditCardResponse response = CreditCardMapper.mapperToResponse(creditCardEntity);
		logger.info("Credit card updated successfully: {}", response);
		return response;
	}

	/**
	 * Deletes a credit card.
	 *
	 * @param creditCardId The credit card ID
	 */
	@Override
	public void deleteCreditCard(String creditCardId) {
		logger.debug("Deleting credit card with ID: {}", creditCardId);
		CreditCardEntity creditCardEntity = creditCardRepository.findById(creditCardId).orElseThrow(() -> {
			logger.error("Credit card not found: {}", creditCardId);
			return new RuntimeException("Tarjeta de crédito no encontrada");
		});

		creditCardEntity.setIsActive(false);
		creditCardEntity.setUpdateDate(LocalDateTime.now());
		creditCardEntity = creditCardRepository.save(creditCardEntity);
		logger.info("Credit card deleted successfully with ID: {}", creditCardId);
	}
}
