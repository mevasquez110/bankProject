package com.nttdata.bank.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nttdata.bank.entity.CreditEntity;
import com.nttdata.bank.entity.PaymentScheduleEntity;
import com.nttdata.bank.mapper.CreditMapper;
import com.nttdata.bank.repository.CreditCardRepository;
import com.nttdata.bank.repository.CreditRepository;
import com.nttdata.bank.repository.PaymentScheduleRepository;
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
	private PaymentScheduleRepository paymentScheduleRepository;

	@Override
	public CreditResponse grantCredit(CreditRequest creditRequest) {
		logger.debug("Granting credit: {}", creditRequest);
		validateCredit(creditRequest);
		CreditEntity creditEntity = CreditMapper.mapperToEntity(creditRequest);
		creditEntity.setIsActive(true);
		creditEntity.setCreateDate(LocalDateTime.now());
		creditEntity = creditRepository.save(creditEntity).block();
		List<PaymentScheduleEntity> schedule = generatePaymentSchedule(creditEntity);
		paymentScheduleRepository.saveAll(schedule);
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

		Optional.ofNullable(creditCardRepository.findByDocumentNumberAndIsActiveTrue(
				creditRequest.getDocumentNumber())
				.toFuture().join()).ifPresent(card -> {
					Optional<Boolean> hasActiveDebt = Optional.ofNullable(
							paymentScheduleRepository.existsByCreditCardNumberAndPaidFalseAndPaymentDateBefore(
									card.getCreditCardNumber(), LocalDate.now()).block());
					hasActiveDebt.ifPresent(debt -> {
						if (debt) {
							throw new RuntimeException("The client has active debt");
						}
					});
				});
	}

	private List<PaymentScheduleEntity> generatePaymentSchedule(CreditEntity creditEntity) {
		logger.debug("Generating payment schedule for credit: {}", creditEntity.getCreditId());
		List<PaymentScheduleEntity> schedule = new ArrayList<>();
		LocalDate firstPaymentDate = LocalDate.now().withDayOfMonth(creditEntity.getPaymentDay());
		Double monthlyInterestRate = Utility.getMonthlyInterestRate(creditEntity.getAnnualInterestRate());

		Double fixedInstallment = Utility.calculateInstallmentAmount(creditEntity.getAmount(), monthlyInterestRate,
				creditEntity.getNumberOfInstallments());

		Double remainingPrincipal = creditEntity.getAmount();

		for (int i = 1; i <= creditEntity.getNumberOfInstallments(); i++) {
			PaymentScheduleEntity payment = new PaymentScheduleEntity();
			payment.setPaymentDate(firstPaymentDate.plusMonths(i - 1));
			Double interestPayment = remainingPrincipal * monthlyInterestRate;
			payment.setDebtAmount(remainingPrincipal - (fixedInstallment - interestPayment));
			payment.setSharePayment(fixedInstallment);
			payment.setCreditId(creditEntity.getCreditId());
			payment.setPaid(false);
			remainingPrincipal -= fixedInstallment - interestPayment;
			schedule.add(payment);
		}

		logger.info("Payment schedule generated for credit: {}", creditEntity.getCreditId());
		return schedule;
	}

	@Override
	public CreditDebtResponse checkDebtCredit(String creditId) {
		logger.debug("Checking debt for credit: {}", creditId);
		creditRepository.findById(creditId).blockOptional().orElseThrow(() -> {
			logger.error("Credit not found: {}", creditId);
			return new RuntimeException("Crédito no encontrado");
		});

		List<PaymentScheduleEntity> paymentSchedule = paymentScheduleRepository.findByCreditIdAndPaidFalse(creditId)
				.collectList().block();

		LocalDate today = LocalDate.now();
		int currentMonth = today.getMonthValue();
		int currentYear = today.getYear();

		Double totalDebt = paymentSchedule.stream().filter(payment -> !payment.getPaid())
				.mapToDouble(PaymentScheduleEntity::getDebtAmount).sum();

		Double share = paymentSchedule.stream().filter(payment -> !payment.getPaid())
				.filter(payment -> payment.getPaymentDate().getMonthValue() == currentMonth
						&& payment.getPaymentDate().getYear() == currentYear)
				.mapToDouble(PaymentScheduleEntity::getSharePayment).sum();

		CreditDebtResponse response = new CreditDebtResponse();
		response.setCreditId(creditId);
		response.setTotalDebt(totalDebt);
		response.setShare(share);
		logger.info("Debt checked successfully for credit: {}", creditId);
		return response;
	}

	@Override
	public List<CreditResponse> findAllCredits() {
		logger.debug("Finding all credits");

		List<CreditResponse> credits = creditRepository.findByIsActiveTrue().map(CreditMapper::mapperToResponse)
				.collectList().block();

		logger.info("All credits retrieved successfully");
		return credits;
	}

}
