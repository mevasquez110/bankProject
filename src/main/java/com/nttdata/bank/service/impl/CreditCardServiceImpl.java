package com.nttdata.bank.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nttdata.bank.entity.CreditCardEntity;
import com.nttdata.bank.entity.PaymentScheduleEntity;
import com.nttdata.bank.mapper.CreditCardMapper;
import com.nttdata.bank.repository.CreditCardRepository;
import com.nttdata.bank.repository.CreditRepository;
import com.nttdata.bank.repository.PaymentScheduleRepository;
import com.nttdata.bank.request.CreditCardRequest;
import com.nttdata.bank.response.CreditCardDebtResponse;
import com.nttdata.bank.response.CreditCardResponse;
import com.nttdata.bank.service.CreditCardService;
import com.nttdata.bank.util.Constants;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
	private CreditRepository creditRepository;
	
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
		validateCreditCard(creditCardRequest);
		CreditCardEntity creditCardEntity = CreditCardMapper.mapperToEntity(creditCardRequest);
		creditCardEntity.setCreditCardNumber(generateUniqueCreditCardNumber(creditCardRequest.getDocumentNumber()));
		creditCardEntity.setCreateDate(LocalDateTime.now());
		creditCardEntity.setIsActive(true);
		creditCardEntity.setAllowConsumption(true);
		CreditCardEntity savedCard = creditCardRepository.save(creditCardEntity).block();
		CreditCardResponse response = CreditCardMapper.mapperToResponse(savedCard);
		logger.info("Credit card created successfully: {}", response);
		return response;
	}

	/**
	 * Validates the credit card details provided in the request. This method checks
	 * if the customer has an active credit card and any active debt associated with
	 * it.
	 *
	 * @param creditCardRequest the credit card request containing the document
	 *                          number and other details
	 * @throws RuntimeException if the customer already has an active credit card or
	 *                          active debt
	 */
	private void validateCreditCard(CreditCardRequest creditCardRequest) {
		Boolean existingCreditCard = creditCardRepository
				.existsByDocumentNumberAndIsActiveTrue(creditCardRequest.getDocumentNumber()).block();

		if (existingCreditCard) {
			throw new RuntimeException("The customer already has a credit card");
		}
		
		Optional.ofNullable(creditRepository.findByDocumentNumberAndIsActiveTrue(
				creditCardRequest.getDocumentNumber())
				.toFuture().join()).ifPresent(card -> {
					Optional<Boolean> hasActiveDebt = Optional.ofNullable(
							paymentScheduleRepository.existsByCreditIdAndPaidFalseAndPaymentDateBefore(
									card.getCreditId(), LocalDate.now()).block());
					hasActiveDebt.ifPresent(debt -> {
						if (debt) {
							throw new RuntimeException("The client has active debt");
						}
					});
				});
	}

	/**
	 * Checks the debt of a credit card.
	 *
	 * @param creditCardNumber The credit card number
	 * @return The credit card debt response
	 */
	@Override
	public CreditCardDebtResponse checkDebtCreditCard(String creditCardNumber) {
		logger.debug("Checking debt for credit card: {}", creditCardNumber);

		CreditCardEntity creditCard = creditCardRepository.findByCreditCardNumberAndIsActiveTrue(creditCardNumber)
				.blockOptional().orElseThrow(() -> {
					logger.error("Credit card not found: {}", creditCardNumber);
					return new RuntimeException("Credit card not found");
				});

		List<PaymentScheduleEntity> paymentSchedules = paymentScheduleRepository
				.findByCreditCardNumberAndPaidFalse(creditCardNumber).collectList().block();

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
		logger.info("Debt checked successfully for credit card: {}", creditCardNumber);
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

		List<CreditCardResponse> creditCards = creditCardRepository.findByIsActiveTrue()
				.map(CreditCardMapper::mapperToResponse).collectList().block();

		logger.info("All credit cards retrieved successfully");
		return creditCards;
	}

	/**
	 * Updates a credit card.
	 *
	 * @param creditCardNumber The credit card number
	 * @return The updated credit card response
	 */
	@Override
	public CreditCardResponse updateCreditCard(String creditCardNumber) {
		logger.debug("Updating credit card with ID: {}", creditCardNumber);

		CreditCardEntity creditCardEntity = creditCardRepository.findByCreditCardNumberAndIsActiveTrue(creditCardNumber)
				.blockOptional().orElseThrow(() -> {
					logger.error("Credit card not found: {}", creditCardNumber);
					return new RuntimeException("Tarjeta de crédito no encontrada");
				});

		creditCardEntity.setAllowConsumption(!creditCardEntity.getAllowConsumption());
		creditCardEntity.setUpdateDate(LocalDateTime.now());
		creditCardEntity = creditCardRepository.save(creditCardEntity).block();
		CreditCardResponse response = CreditCardMapper.mapperToResponse(creditCardEntity);
		logger.info("Credit card updated successfully: {}", response);
		return response;
	}

	/**
	 * Deletes a credit card.
	 *
	 * @param creditCardNumber The credit card number
	 */
	@Override
	public void deleteCreditCard(String creditCardNumber) {
		logger.debug("Deleting credit card with ID: {}", creditCardNumber);

		CreditCardEntity creditCardEntity = creditCardRepository.findByCreditCardNumberAndIsActiveTrue(creditCardNumber)
				.blockOptional().orElseThrow(() -> {
					logger.error("Credit card not found: {}", creditCardNumber);
					return new RuntimeException("Tarjeta de crédito no encontrada");
				});

		creditCardEntity.setIsActive(false);
		creditCardEntity.setUpdateDate(LocalDateTime.now());
		creditCardEntity = creditCardRepository.save(creditCardEntity).block();
		logger.info("Credit card deleted successfully with ID: {}", creditCardNumber);
	}

	/**
	 * Generates a unique document number.
	 *
	 * @param documentNumber The document Number
	 * @return The generated unique account number
	 */
	private String generateUniqueCreditCardNumber(String documentNumber) {
		String creditCardNumber;
		boolean exists;
		do {
			creditCardNumber = Constants.CREDIT_TYPE + Constants.BANK_CODE + documentNumber + generateRandomNumber();
			exists = creditCardRepository.existsByCreditCardNumberAndIsActiveTrue(creditCardNumber).toFuture().join();
		} while (exists);
		return creditCardNumber;
	}

	/**
	 * Generates a random number to append to the credit card number.
	 * 
	 * @return A random number as a string.
	 */
	private String generateRandomNumber() {
		int randomNumber = (int) (Math.random() * 10000);
		return String.format("%04d", randomNumber);
	}

}
