package com.nttdata.bank.service.impl;

import java.time.LocalDate;
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
		boolean hasActiveCredit = creditRepository.existsByCustomerIdAndStatus(creditRequest.getCustomerId(),
				"APPROVED");

		if (hasActiveCredit) {
			throw new RuntimeException("El cliente ya tiene un crédito activo");
		}

		CreditEntity creditEntity = CreditMapper.mapperToEntity(creditRequest);
		creditEntity.setStatus("APPROVED");
		creditEntity = creditRepository.save(creditEntity);
		List<PaymentScheduleEntity> schedule = generatePaymentSchedule(creditEntity);
		paymentScheduleRepository.saveAll(schedule);
		return CreditMapper.mapperToResponse(creditEntity);
	}

	private List<PaymentScheduleEntity> generatePaymentSchedule(CreditEntity creditEntity) {
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

		return schedule;
	}

	@Override
	public CreditDebtResponse checkDebt(String creditId) {
		creditRepository.findById(creditId).orElseThrow(() -> new RuntimeException("Crédito no encontrado"));
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
		return response;
	}

}
