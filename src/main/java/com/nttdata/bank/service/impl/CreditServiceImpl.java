package com.nttdata.bank.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nttdata.bank.entity.CreditCardScheduleEntity;
import com.nttdata.bank.entity.CreditEntity;
import com.nttdata.bank.entity.CreditScheduleEntity;
import com.nttdata.bank.mapper.CreditMapper;
import com.nttdata.bank.repository.CreditCardRepository;
import com.nttdata.bank.repository.CreditCardScheduleRepository;
import com.nttdata.bank.repository.CreditRepository;
import com.nttdata.bank.repository.CreditScheduleRepository;
import com.nttdata.bank.request.CreditRequest;
import com.nttdata.bank.response.CreditResponse;
import com.nttdata.bank.response.CreditDebtResponse;
import com.nttdata.bank.service.CreditService;
import com.nttdata.bank.util.Utility;

/**
 * * CreditServiceImpl is the implementation class for the CreditService
 * interface. * This class provides the actual logic for handling credit-related
 * operations such as granting a credit, * checking credit debt, updating
 * reprogrammed debt, finding all credits, and deleting a credit.
 */

@Service
public class CreditServiceImpl implements CreditService {

	private static final Logger logger = LoggerFactory.getLogger(CreditServiceImpl.class);

	@Autowired
	private CreditRepository creditRepository;

	@Autowired
	private CreditCardRepository creditCardRepository;

	@Autowired
	private CreditScheduleRepository creditScheduleRepository;

	@Autowired
	private CreditCardScheduleRepository creditCardScheduleRepository;

	/**
	 * Grants credit based on the provided credit request. This method handles the
	 * validation of the credit request, maps the request to a credit entity,
	 * activates and timestamps the entity, saves it to the repository, generates a
	 * payment schedule, and maps the entity to a response.
	 *
	 * @param creditRequest the credit request containing the details for granting
	 *                      credit
	 * @return CreditResponse containing the details of the granted credit
	 * @throws RuntimeException if validation of the credit request fails
	 */
	@Override
	public CreditResponse grantCredit(CreditRequest creditRequest) {
		logger.debug("Granting credit: {}", creditRequest);
		validateCredit(creditRequest);
		CreditEntity creditEntity = CreditMapper.mapperToEntity(creditRequest);
		creditEntity.setIsActive(true);
		creditEntity.setCreateDate(LocalDateTime.now());
		creditEntity = creditRepository.save(creditEntity).block();
		List<CreditScheduleEntity> schedule = generatePaymentSchedule(creditEntity);
		creditScheduleRepository.saveAll(schedule);
		CreditResponse response = CreditMapper.mapperToResponse(creditEntity);
		logger.info("Credit granted successfully: {}", response);
		return response;
	}

	/**
	 * Validates the credit request details provided by the customer. This method
	 * checks if the customer has any active credit that would prevent them from
	 * being approved for new credit.
	 *
	 * @param creditRequest the credit request containing the document number and
	 *                      other relevant details
	 * @throws RuntimeException if the customer already has an active credit
	 */
	private void validateCredit(CreditRequest creditRequest) {
		Boolean existingCredit = creditRepository
				.existsByDocumentNumberAndIsActiveTrue(creditRequest.getDocumentNumber()).block();

		if (existingCredit) {
			throw new RuntimeException("The customer already has an active credit");
		}

		Optional.ofNullable(
				creditCardRepository.findByDocumentNumberAndIsActiveTrue(creditRequest.getDocumentNumber()).block())
				.ifPresent(creditCardEntity -> {
					List<CreditCardScheduleEntity> paymentSchedule = creditCardScheduleRepository
							.findByCreditCardAndPaidFalseAndPaymentDateLessThanEqual(
									creditCardEntity.getCreditCardNumber(), LocalDate.now())
							.collectList().block();

					if (paymentSchedule.isEmpty()) {
						throw new RuntimeException("The client has active debt");
					}
				});
	}

	/**
	 * Generates a payment schedule for the given credit entity. This method
	 * initializes the payment schedule list, calculates the first payment date,
	 * computes the monthly interest rate, calculates the fixed installment amount,
	 * iterates over the number of installments to create each payment schedule
	 * entry, updates the debt amount and adjusts the remaining principal, adds each
	 * payment schedule entry to the list, and logs the successful generation of the
	 * payment schedule.
	 *
	 * @param creditEntity the credit entity for which the payment schedule is to be
	 *                     generated
	 * @return List of PaymentScheduleEntity representing the payment schedule
	 */
	private List<CreditScheduleEntity> generatePaymentSchedule(CreditEntity creditEntity) {
		logger.debug("Generating payment schedule for credit: {}", creditEntity.getCreditId());
		List<CreditScheduleEntity> schedule = new ArrayList<>();
		LocalDate firstPaymentDate = LocalDate.now().withDayOfMonth(creditEntity.getPaymentDay());
		Double monthlyInterestRate = Utility.getMonthlyInterestRate(creditEntity.getAnnualInterestRate());
		Double totalDebt = creditEntity.getAmount();
		Double principalAmount = totalDebt / creditEntity.getNumberOfInstallments();

		for (int i = 1; i <= creditEntity.getNumberOfInstallments(); i++) {
			Double interestPayment = totalDebt * monthlyInterestRate;
			Double currentDebt = principalAmount + interestPayment;
			CreditScheduleEntity payment = new CreditScheduleEntity();
			payment.setPaymentDate(firstPaymentDate.plusMonths(i - 1));
			payment.setInterestAmount(interestPayment);
			payment.setPrincipalAmount(principalAmount);
			payment.setCurrentDebt(currentDebt);
			payment.setTotalDebt(totalDebt);
			payment.setCreditId(creditEntity.getCreditId());
			payment.setPaid(false);
			schedule.add(payment);
			totalDebt -= principalAmount;
		}

		logger.info("Payment schedule generated for credit: {}", creditEntity.getCreditId());
		return schedule;
	}

	/**
	 * Checks the debt for a given credit ID. This method finds the credit entity by
	 * ID, retrieves the unpaid payment schedule entries, calculates the total debt
	 * and the share for the current month, and returns the debt details in a
	 * response.
	 *
	 * @param creditId the credit ID for which to check the debt
	 * @return CreditDebtResponse containing the total debt and the share for the
	 *         current month
	 * @throws RuntimeException if the credit is not found
	 */
	@Override
	public CreditDebtResponse checkDebtCredit(String creditId) {
		logger.debug("Checking debt for credit: {}", creditId);

		creditRepository.findByIdAndIsActiveTrue(creditId).blockOptional().orElseThrow(() -> {
			logger.error("Credit not found: {}", creditId);
			return new RuntimeException("Credit not found");
		});

		List<CreditScheduleEntity> paymentSchedule = creditScheduleRepository
				.findByCreditIdAndPaidFalseAndPaymentDateLessThanEqual(creditId, LocalDate.now()).collectList().block();

		if (paymentSchedule != null && !paymentSchedule.isEmpty()) {
			Double totalDebt = paymentSchedule.stream()
					.max(Comparator.comparingDouble(CreditScheduleEntity::getTotalDebt))
					.map(CreditScheduleEntity::getTotalDebt).get();

			Double share = paymentSchedule.stream().mapToDouble(CreditScheduleEntity::getCurrentDebt).sum();

			CreditDebtResponse response = new CreditDebtResponse();
			response.setCreditId(creditId);
			response.setTotalDebt(totalDebt);
			response.setShare(share);

			logger.info("Debt checked successfully for credit: {}", creditId);
			return response;
		} else {
			logger.error("No payment schedule found for credit: {}", creditId);
			throw new RuntimeException("No payment schedule found");
		}
	}

	/**
	 * Retrieves all active credits. This method finds all credits that are active,
	 * maps them to credit response objects, and returns the list of credit
	 * responses.
	 *
	 * @return List of CreditResponse containing details of all active credits
	 */
	@Override
	public List<CreditResponse> findAllCredits() {
		logger.debug("Finding all credits");
		return creditRepository.findByIsActiveTrue().map(CreditMapper::mapperToResponse).collectList().block();
	}

}
