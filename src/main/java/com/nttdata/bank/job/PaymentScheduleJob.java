package com.nttdata.bank.job;

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

@Component
public class PaymentScheduleJob {

	@Autowired
	private PaymentScheduleRepository paymentScheduleRepository;

	@Autowired
	private CreditRepository creditRepository;

	@Scheduled(cron = "0 0 0 * * ?")
	public void updatePaymentSchedules() {
		LocalDate today = LocalDate.now();
		List<PaymentScheduleEntity> overduePayments = paymentScheduleRepository
				.findByPaymentDateBeforeAndPaidFalse(today);

		for (PaymentScheduleEntity payment : overduePayments) {
			updatePayment(payment, today);
		}
	}

	private void updatePayment(PaymentScheduleEntity payment, LocalDate today) {
		creditRepository.findById(payment.getCreditId()).ifPresent(credit -> {
			Double dailyInterestRate = Utility.getDailyInterestRate(credit.getAnnualLateInterestRate());
			
			Double overdueInterest = payment.getDebtAmount() * dailyInterestRate
					* ChronoUnit.DAYS.between(payment.getPaymentDate(), today);
			
			payment.setDebtAmount(payment.getDebtAmount() + overdueInterest);
			paymentScheduleRepository.save(payment);
		});
	}
}
