package com.nttdata.bank.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.nttdata.bank.entity.PaymentScheduleEntity;
import com.nttdata.bank.repository.CreditRepository;
import com.nttdata.bank.repository.PaymentScheduleRepository;
import com.nttdata.bank.util.Utility;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * PaymentScheduleJob is a scheduled task that updates payment schedules. This
 * class runs a scheduled job daily at midnight to check for overdue payments
 * and updates them with overdue interest. It uses Spring's scheduling
 * annotations and relies on repositories for accessing and updating payment
 * schedules and credits.
 */

@Component
public class PaymentScheduleJob {

	private static final Logger logger = LoggerFactory.getLogger(PaymentScheduleJob.class);

	@Autowired
	private PaymentScheduleRepository paymentScheduleRepository;

	@Autowired
	private CreditRepository creditRepository;

	/**
	 * Scheduled job to update payment schedules. This method is scheduled to run
	 * daily at midnight.
	 */
	@Scheduled(cron = "0 0 0 * * ?")
	public void updatePaymentSchedules() {
		LocalDate today = LocalDate.now();
		logger.debug("Updating payment schedules for date: {}", today);
		List<PaymentScheduleEntity> list = paymentScheduleRepository.findByPaidFalseAndPaymentDateBefore(today);

		for (PaymentScheduleEntity payment : list) {
			updatePayment(payment, today);
		}
	}

	/**
	 * Updates an individual payment schedule.
	 *
	 * @param payment The payment schedule entity to update
	 * @param today   The current date
	 */
	private void updatePayment(PaymentScheduleEntity payment, LocalDate today) {
		logger.debug("Updating payment schedule for payment ID: {}", payment.getId());
		creditRepository.findById(payment.getCreditId()).ifPresent(credit -> {
			Double dailyInterestRate = Utility.getDailyInterestRate(credit.getAnnualLateInterestRate());

			Double overdueInterest = payment.getDebtAmount() * dailyInterestRate
					* ChronoUnit.DAYS.between(payment.getPaymentDate(), today);

			payment.setDebtAmount(payment.getDebtAmount() + overdueInterest);
			paymentScheduleRepository.save(payment);
			logger.info("Updated payment schedule for payment ID: {}", payment.getId());
		});
	}
}
