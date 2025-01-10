package com.nttdata.bank.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.nttdata.bank.entity.PaymentScheduleEntity;
import reactor.core.publisher.Flux;
import java.time.LocalDate;

/**
 * PaymentScheduleRepository provides the CRUD operations for
 * PaymentScheduleEntity. This interface extends ReactiveMongoRepository and
 * defines custom query methods to find payment schedules by credit ID, find
 * payment schedules by credit card number and date range, find unpaid payment
 * schedules before a specific date, and find payment schedules by credit card
 * number.
 */
public interface PaymentScheduleRepository extends ReactiveMongoRepository<PaymentScheduleEntity, String> {

	/**
	 * Finds payment schedules by credit ID.
	 *
	 * @param creditId The credit ID to search for.
	 * @return A Flux emitting PaymentScheduleEntity objects.
	 */
	Flux<PaymentScheduleEntity> findByCreditId(String creditId);

	/**
	 * Finds payment schedules by credit card number and date range.
	 *
	 * @param creditCardNumber The credit card number to search for.
	 * @param startDate        The start date of the range.
	 * @param endDate          The end date of the range.
	 * @return A Flux emitting PaymentScheduleEntity objects.
	 */
	Flux<PaymentScheduleEntity> findByCreditCardNumberAndPaymentDateBetween(String creditCardNumber,
			LocalDate startDate, LocalDate endDate);

	/**
	 * Finds unpaid payment schedules before a specific date.
	 *
	 * @param date The date to search for unpaid schedules before.
	 * @return A Flux emitting unpaid PaymentScheduleEntity objects.
	 */
	Flux<PaymentScheduleEntity> findByPaidFalseAndPaymentDateBefore(LocalDate date);

	/**
	 * Finds payment schedules by credit card number.
	 *
	 * @param creditCardNumber The credit card number to search for.
	 * @return A Flux emitting PaymentScheduleEntity objects.
	 */
	Flux<PaymentScheduleEntity> findByCreditCardNumber(String creditCardNumber);
}
