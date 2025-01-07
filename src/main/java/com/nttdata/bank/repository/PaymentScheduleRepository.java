package com.nttdata.bank.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.nttdata.bank.entity.PaymentScheduleEntity;

import java.time.LocalDate;
import java.util.List;

public interface PaymentScheduleRepository extends MongoRepository<PaymentScheduleEntity, String> {

	List<PaymentScheduleEntity> findByCreditId(String creditId);

	List<PaymentScheduleEntity> findByCreditCardNumberAndPaymentDateBetween(String creditCardNumber,
			LocalDate startDate, LocalDate endDate);
}
