package com.nttdata.bank.job;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.nttdata.bank.entity.Consumption;
import com.nttdata.bank.entity.CreditCardEntity;
import com.nttdata.bank.entity.CreditCardScheduleEntity;
import com.nttdata.bank.entity.CreditEntity;
import com.nttdata.bank.entity.CreditScheduleEntity;
import com.nttdata.bank.repository.CreditCardRepository;
import com.nttdata.bank.repository.CreditCardScheduleRepository;
import com.nttdata.bank.repository.CreditRepository;
import com.nttdata.bank.repository.CreditScheduleRepository;
import com.nttdata.bank.util.Utility;

/**
 * PaymentScheduleJob is a scheduled task that updates payment schedules. This
 * class runs a scheduled job daily at midnight to check for overdue payments
 * and updates them with overdue interest. It uses Spring's scheduling
 * annotations and relies on repositories for accessing and updating payment
 * schedules and credits.
 */

@Component
public class PaymentScheduleJob {

	@Autowired
	private CreditScheduleRepository creditScheduleRepository;

	@Autowired
	private CreditRepository creditRepository;

	@Autowired
	private CreditCardRepository creditCardRepository;

	@Autowired
	private CreditCardScheduleRepository creditCardScheduleRepository;

	/**
	 * Scheduled job to update payment schedules. This method is scheduled to run
	 * daily at midnight.
	 */
	@Scheduled(cron = "0 0 0 * * ?")
	public void updateTotalDebtCreditSchedules() {
		List<CreditScheduleEntity> paymentScheduleShare = creditScheduleRepository
				.findByPaidFalseAndPaymentDateLessThanEqual(LocalDateTime.now()).collectList().block();

		Map<String, List<CreditScheduleEntity>> groupedAndSortedSchedules = paymentScheduleShare.stream()
				.collect(Collectors.groupingBy(CreditScheduleEntity::getId,
						Collectors.collectingAndThen(Collectors.toList(),
								list -> list.stream().sorted(Comparator.comparing(CreditScheduleEntity::getPaymentDate))
										.collect(Collectors.toList()))));

		for (Map.Entry<String, List<CreditScheduleEntity>> entry : groupedAndSortedSchedules.entrySet()) {
			List<CreditScheduleEntity> overduePaymentSchedule = entry.getValue();
			Double share = overduePaymentSchedule.stream().mapToDouble(CreditScheduleEntity::getCurrentDebt).sum();
			String creditId = entry.getKey();

			List<CreditScheduleEntity> upcomingPaymentSchedule = creditScheduleRepository
					.findByCreditIdAndPaidFalseAndPaymentDateAfter(creditId, LocalDateTime.now()).collectList().block();

			Double totalDebt = upcomingPaymentSchedule.stream().mapToDouble(CreditScheduleEntity::getCurrentDebt).sum()
					+ share;

			CreditEntity creditEntity = creditRepository.findByIdAndIsActiveTrue(creditId).block();
			Double monthlyLateInterestRate = Utility.getMonthlyInterestRate(creditEntity.getAnnualLateInterestRate());

			for (CreditScheduleEntity creditScheduleEntity : overduePaymentSchedule) {
				Double lateAmountDay = totalDebt * monthlyLateInterestRate;
				creditScheduleEntity.setLateAmount(creditScheduleEntity.getLateAmount() + lateAmountDay);

				creditScheduleEntity.setCurrentDebt(creditScheduleEntity.getPrincipalAmount()
						+ creditScheduleEntity.getInterestAmount() + creditScheduleEntity.getLateAmount());

				totalDebt = totalDebt - creditScheduleEntity.getPrincipalAmount();
				creditScheduleRepository.save(creditScheduleEntity);
			}

			for (CreditScheduleEntity creditScheduleEntity : upcomingPaymentSchedule) {
				totalDebt = totalDebt - creditScheduleEntity.getPrincipalAmount();
				creditScheduleRepository.save(creditScheduleEntity);

			}
		}
	}

	/**
	 * Scheduled job to update payment schedules. This method is scheduled to run
	 * daily at midnight.
	 */
	@Scheduled(cron = "0 0 0 * * ?")
	public void updateTotalDebtCreditCardSchedules() {
		List<CreditCardScheduleEntity> paymentScheduleShare = creditCardScheduleRepository
				.findByPaidFalseAndPaymentDateLessThanEqual(LocalDateTime.now()).collectList().block();

		Map<String, List<CreditCardScheduleEntity>> groupedAndSortedSchedules = paymentScheduleShare.stream()
				.collect(Collectors.groupingBy(CreditCardScheduleEntity::getId,
						Collectors.collectingAndThen(Collectors.toList(),
								list -> list.stream()
										.sorted(Comparator.comparing(CreditCardScheduleEntity::getPaymentDate))
										.collect(Collectors.toList()))));

		for (Map.Entry<String, List<CreditCardScheduleEntity>> entry : groupedAndSortedSchedules.entrySet()) {
			List<CreditCardScheduleEntity> overduePaymentSchedule = entry.getValue();
			Double share = overduePaymentSchedule.stream().mapToDouble(CreditCardScheduleEntity::getCurrentDebt).sum();
			String creditCardNumber = entry.getKey();

			CreditCardEntity creditCardEntity = creditCardRepository
					.findByCreditCardNumberAndIsActiveTrue(creditCardNumber).block();

			Double monthlyLateInterestRate = Utility
					.getMonthlyInterestRate(creditCardEntity.getAnnualLateInterestRate());

			for (CreditCardScheduleEntity creditCardScheduleEntity : overduePaymentSchedule) {
				Double lateAmountDay = share * monthlyLateInterestRate;
				creditCardScheduleEntity.setLateAmount(creditCardScheduleEntity.getLateAmount() + lateAmountDay);

				creditCardScheduleEntity.setCurrentDebt(creditCardScheduleEntity.getPrincipalAmount()
						+ creditCardScheduleEntity.getInterestAmount() + creditCardScheduleEntity.getLateAmount());

				share = share - creditCardScheduleEntity.getPrincipalAmount();
				creditCardScheduleRepository.save(creditCardScheduleEntity);
			}
		}
	}

	/**
	 * Scheduled job to generate the monthly credit card schedule. This method is
	 * scheduled to run on the 21st day of every month at midnight.
	 */
	@Scheduled(cron = "0 0 0 21 * ?")
	public void generateMonthlyCreditCardSchedule() {
		List<CreditCardScheduleEntity> schedule = creditCardScheduleRepository.findByPaymentDate(LocalDateTime.now())
				.collectList().block();

		for (CreditCardScheduleEntity creditCardScheduleEntity : schedule) {
			CreditCardEntity creditCardEntity = creditCardRepository
					.findByCreditCardNumberAndIsActiveTrue(creditCardScheduleEntity.getCreditCardNumber()).block();

			for (Consumption consumption : creditCardScheduleEntity.getConsumptionQuota()) {
				Integer numberInstallments = consumption.getNumberOfInstallments();

				if (numberInstallments > 1) {
					Integer numberInstallmentsTotal = numberInstallments - 1;
					int e = 0;

					for (int i = numberInstallmentsTotal; i >= 0; i--) {
						CreditCardScheduleEntity entity = creditCardScheduleRepository
								.findByCreditCardNumberAndPaymentDate(creditCardEntity.getCreditCardNumber(),
										creditCardScheduleEntity.getPaymentDate().plusMonths(1 + e))
								.block();

						if (entity == null) {
							entity = new CreditCardScheduleEntity();
							entity.setCreditCardNumber(creditCardEntity.getCreditCardNumber());
							entity.setPaymentDate(creditCardScheduleEntity.getPaymentDate().plusMonths(1 + e));
							entity.setPaid(false);
						}

						consumption.setNumberOfInstallments(1);
						entity.getConsumptionQuota().add(consumption);
						creditCardScheduleRepository.save(entity);
						e++;
					}

				}

				consumption.setNumberOfInstallments(1);
			}

			double principalAmount = creditCardScheduleEntity.getConsumptionQuota().stream()
					.mapToDouble(Consumption::getAmount).sum();

			List<CreditCardScheduleEntity> overduePaymentSchedule = creditCardScheduleRepository
					.findByCreditCardNumberAndPaidFalseAndPaymentDateLessThanEqual(
							creditCardEntity.getCreditCardNumber(), LocalDate.now())
					.collectList().block();

			Double share = overduePaymentSchedule.stream().mapToDouble(CreditCardScheduleEntity::getCurrentDebt).sum();
			Double totalDebt = creditCardScheduleEntity.getCurrentDebt() + share;
			Double monthlyInterestRate = Utility.getMonthlyInterestRate(creditCardEntity.getAnnualInterestRate());
			creditCardScheduleEntity.setPrincipalAmount(principalAmount);
			creditCardScheduleEntity.setInterestAmount(monthlyInterestRate * totalDebt);
			creditCardScheduleEntity.setLateAmount(0.00);

			creditCardScheduleEntity.setCurrentDebt(creditCardScheduleEntity.getPrincipalAmount()
					+ creditCardScheduleEntity.getInterestAmount() + creditCardScheduleEntity.getLateAmount());

			creditCardScheduleRepository.save(creditCardScheduleEntity);
		}
	}
}
