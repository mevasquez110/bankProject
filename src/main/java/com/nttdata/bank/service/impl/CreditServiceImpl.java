package com.nttdata.bank.service.impl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nttdata.bank.entity.CreditEntity;
import com.nttdata.bank.entity.PaymentScheduleEntity;
import com.nttdata.bank.mapper.CreditMapper;
import com.nttdata.bank.repository.CreditRepository;
import com.nttdata.bank.repository.PaymentScheduleRepository;
import com.nttdata.bank.request.CreditRequest;
import com.nttdata.bank.response.CreditResponse;
import com.nttdata.bank.response.CreditDebtResponse;
import com.nttdata.bank.service.CreditService;
import com.nttdata.bank.util.Utility;

@Service
public class CreditServiceImpl implements CreditService {

	@Autowired
	private CreditRepository creditRepository;

	@Autowired
	private PaymentScheduleRepository paymentScheduleRepository;

	@Override
	public CreditResponse grantCredit(CreditRequest creditRequest) {
		CreditEntity creditEntity = CreditMapper.mapperToEntity(creditRequest);
		creditEntity.setStatus("APPROVED");
		creditEntity = creditRepository.save(creditEntity);

		List<PaymentScheduleEntity> schedule = generatePaymentSchedule(creditEntity);

		paymentScheduleRepository.saveAll(schedule);
		return CreditMapper.mapperToResponse(creditEntity);
	}

	private List<PaymentScheduleEntity> generatePaymentSchedule(CreditEntity creditEntity) {
		List<PaymentScheduleEntity> schedule = new ArrayList<>();
		Double monthlyInterestRate = Utility.getMonthlyInterestRate(creditEntity.getAnnualInterestRate());
		
		Double installmentAmount = Utility.calculateInstallmentAmount(creditEntity.getAmount(),
				creditEntity.getAnnualInterestRate(), creditEntity.getNumberOfInstallments());
		
		LocalDate firstPaymentDate = LocalDate.now().withDayOfMonth(creditEntity.getPaymentDay());
		Double amount = creditEntity.getAmount();

		for (int i = 1; i <= creditEntity.getNumberOfInstallments(); i++) {
			PaymentScheduleEntity payment = new PaymentScheduleEntity();
			payment.setPaymentDate(firstPaymentDate.plusMonths(i - 1));
			Double interestAmount = creditEntity.getAmount() * monthlyInterestRate;
			Double principalAmount = installmentAmount - interestAmount;
			payment.setPrincipalAmount(principalAmount);
			payment.setInterestAmount(interestAmount);
			payment.setTotalPayment(installmentAmount);
			payment.setCreditId(creditEntity.getCreditId());
			payment.setPaid(false);
			schedule.add(payment);
			amount -= principalAmount;
		}

		return schedule;
	}

	@Override
	public CreditDebtResponse checkDebt(String creditId) {
	    List<PaymentScheduleEntity> payments = paymentScheduleRepository.findByCreditId(creditId);
	    
	    if (payments.isEmpty()) {
	        return null; 
	    }

	    CreditEntity creditEntity = creditRepository.findById(creditId).orElseThrow(() -> new RuntimeException("Credit not found"));

	    double totalDebt = 0.0;
	    double share = 0.0;
	    double dailyLateInterestRate = Utility.getDailyInterestRate(creditEntity.getAnnualLateInterestRate());

	    LocalDate now = LocalDate.now();

	    for (PaymentScheduleEntity payment : payments) {
	        if (!payment.getPaid()) {
	            double lateFee = 0.0;
	            
	            if (payment.getPaymentDate().isBefore(now)) {
	                long daysLate = ChronoUnit.DAYS.between(payment.getPaymentDate(), now);
					lateFee = payment.getTotalPayment()
							* Math.pow((1 + dailyLateInterestRate), daysLate) - payment.getTotalPayment();
	            }
	            
	            totalDebt += payment.getTotalPayment() + lateFee;
	            share = payment.getTotalPayment() + lateFee; 
	        }
	    }

	    CreditDebtResponse response = new CreditDebtResponse();
	    response.setCreditId(creditId);
	    response.setTotalDebt(totalDebt);
	    response.setShare(share);
	    return response;
	}

}
