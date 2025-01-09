package com.nttdata.bank.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nttdata.bank.entity.CreditEntity;
import com.nttdata.bank.entity.PaymentScheduleEntity;
import com.nttdata.bank.mapper.CreditMapper;
import com.nttdata.bank.repository.CreditRepository;
import com.nttdata.bank.repository.PaymentScheduleRepository;
import com.nttdata.bank.request.CreditRequest;
import com.nttdata.bank.request.ReprogramDebtRequest;
import com.nttdata.bank.response.CreditResponse;
import com.nttdata.bank.response.CreditDebtResponse;
import com.nttdata.bank.service.CreditService;
import com.nttdata.bank.util.Utility;

@Service
public class CreditServiceImpl implements CreditService {

	private static final Logger logger = LoggerFactory.getLogger(CreditServiceImpl.class);

	@Autowired
	private CreditRepository creditRepository;

	@Autowired
	private PaymentScheduleRepository paymentScheduleRepository;

	/**
	 * Grants a new credit.
	 *
	 * @param creditRequest The credit request payload
	 * @return The credit response
	 */
	@Override
	public CreditResponse grantCredit(CreditRequest creditRequest) {
		logger.debug("Granting credit: {}", creditRequest);
		Boolean hasActiveCredit = creditRepository.existsByCustomerIdAndIsActiveTrue(creditRequest.getCustomerId());

		if (hasActiveCredit) {
			logger.warn("The customer already has an active credit: {}", creditRequest.getCustomerId());
			throw new RuntimeException("El cliente ya tiene un crédito activo");
		}

		CreditEntity creditEntity = CreditMapper.mapperToEntity(creditRequest);
		creditEntity.setIsActive(true);
		creditEntity.setCreateDate(LocalDateTime.now());
		creditEntity = creditRepository.save(creditEntity);
		List<PaymentScheduleEntity> schedule = generatePaymentSchedule(creditEntity);
		paymentScheduleRepository.saveAll(schedule);
		CreditResponse response = CreditMapper.mapperToResponse(creditEntity);
		logger.info("Credit granted successfully: {}", response);
		return response;
	}

	/**
	 * Generates the payment schedule for a credit.
	 *
	 * @param creditEntity The credit entity
	 * @return A list of payment schedule entities
	 */
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

	/**
	 * Checks the debt of a credit.
	 *
	 * @param creditId The credit ID
	 * @return The credit debt response
	 */
	@Override
	public CreditDebtResponse checkDebt(String creditId) {
		logger.debug("Checking debt for credit: {}", creditId);
		creditRepository.findById(creditId).orElseThrow(() -> {
			logger.error("Credit not found: {}", creditId);
			return new RuntimeException("Crédito no encontrado");
		});

		List<PaymentScheduleEntity> paymentSchedule = paymentScheduleRepository.findByCreditId(creditId);
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

	/**
	 * Generates a new payment schedule for reprogrammed debt.
	 *
	 * @param creditEntity   The credit entity
	 * @param unpaidSchedule The list of unpaid payment schedules
	 * @return A list of new payment schedule entities
	 */
	private List<PaymentScheduleEntity> generateNewPaymentSchedule(CreditEntity creditEntity,
			List<PaymentScheduleEntity> unpaidSchedule) {
		logger.debug("Generating new payment schedule for credit: {}", creditEntity.getCreditId());
		List<PaymentScheduleEntity> schedule = new ArrayList<>();
		LocalDate firstPaymentDate = LocalDate.now().withDayOfMonth(creditEntity.getPaymentDay());
		Double monthlyInterestRate = Utility.getMonthlyInterestRate(creditEntity.getAnnualInterestRate());
		Double remainingPrincipal = unpaidSchedule.stream().mapToDouble(PaymentScheduleEntity::getDebtAmount).sum();

		Double fixedInstallment = Utility.calculateInstallmentAmount(remainingPrincipal, monthlyInterestRate,
				creditEntity.getNumberOfInstallments());

		for (int i = 0; i < creditEntity.getNumberOfInstallments(); i++) {
			PaymentScheduleEntity payment = new PaymentScheduleEntity();
			payment.setPaymentDate(firstPaymentDate.plusMonths(i));
			Double interestPayment = remainingPrincipal * monthlyInterestRate;
			payment.setDebtAmount(remainingPrincipal - (fixedInstallment - interestPayment));
			payment.setSharePayment(fixedInstallment);
			payment.setCreditId(creditEntity.getCreditId());
			payment.setPaid(false);
			remainingPrincipal -= fixedInstallment - interestPayment;
			schedule.add(payment);
		}

		logger.info("New payment schedule generated for credit: {}", creditEntity.getCreditId());
		return schedule;
	}

	/**
	 * Updates and reprograms the debt of a credit.
	 *
	 * @param reprogramDebtRequest The reprogram debt request payload
	 * @return The updated credit response
	 */
	@Override
	public CreditResponse updateReprogramDebt(ReprogramDebtRequest reprogramDebtRequest) {
		logger.debug("Reprogramming debt for credit: {}", reprogramDebtRequest.getCreditId());
		
		CreditEntity creditEntity = creditRepository.findByCreditIdAndIsActiveTrue(reprogramDebtRequest.getCreditId())
				.orElseThrow(() -> {
					logger.error("Credit not found: {}", reprogramDebtRequest.getCreditId());
					return new RuntimeException("Crédito no encontrado");
				});

		creditEntity.setAnnualInterestRate(reprogramDebtRequest.getNewInterestRate());
		creditEntity.setAnnualLateInterestRate(reprogramDebtRequest.getNewLateInterestRate());
		creditEntity.setPaymentDay(reprogramDebtRequest.getNewPaymentDay());
		creditEntity.setNumberOfInstallments(reprogramDebtRequest.getNewNumberOfInstallments());
		creditEntity.setUpdateDate(LocalDateTime.now());
		creditEntity = creditRepository.save(creditEntity);

		List<PaymentScheduleEntity> unpaidSchedule = paymentScheduleRepository
				.findByCreditId(reprogramDebtRequest.getCreditId()).stream().filter(payment -> !payment.getPaid())
				.collect(Collectors.toList());

		List<PaymentScheduleEntity> newSchedule = generateNewPaymentSchedule(creditEntity, unpaidSchedule);

		IntStream.range(0, unpaidSchedule.size()).forEach(i -> {
			PaymentScheduleEntity oldPayment = unpaidSchedule.get(i);
			PaymentScheduleEntity newPayment = newSchedule.get(i);
			oldPayment.setPaymentDate(newPayment.getPaymentDate());
			oldPayment.setDebtAmount(newPayment.getDebtAmount());
			oldPayment.setSharePayment(newPayment.getSharePayment());
		});

		if (newSchedule.size() > unpaidSchedule.size()) {
			List<PaymentScheduleEntity> additionalPayments = newSchedule.subList(unpaidSchedule.size(),
					newSchedule.size());

			paymentScheduleRepository.saveAll(additionalPayments);
		}

		paymentScheduleRepository.saveAll(unpaidSchedule);
		CreditResponse response = CreditMapper.mapperToResponse(creditEntity);
		logger.info("Debt reprogrammed successfully for credit: {}", response);
		return response;
	}

	/**
	 * Finds all credits.
	 *
	 * @return A list of credit responses
	 */
	@Override
	public List<CreditResponse> findAllCredits() {
		logger.debug("Finding all credits");
		List<CreditResponse> credits = creditRepository.findByIsActiveTrue().stream().map(CreditMapper::mapperToResponse)
				.collect(Collectors.toList());
		logger.info("All credits retrieved successfully");
		return credits;
	}

	/**
	 * Deletes a credit.
	 *
	 * @param creditId The credit ID
	 */
	@Override
	public void deleteCredit(String creditId) {
		logger.debug("Deleting credit with ID: {}", creditId);
		CreditEntity creditEntity = creditRepository.findByCreditIdAndIsActiveTrue(creditId).orElseThrow(() -> {
			logger.error("Credit not found: {}", creditId);
			return new RuntimeException("Crédito no encontrado");
		});

		creditEntity.setIsActive(false);
		creditEntity.setDeleteDate(LocalDateTime.now());
		creditRepository.save(creditEntity);
		logger.info("Credit deleted successfully with ID: {}", creditId);
	}
}
