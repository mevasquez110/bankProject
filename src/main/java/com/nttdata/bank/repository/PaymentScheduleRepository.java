package com.nttdata.bank.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.nttdata.bank.entity.PaymentScheduleEntity;
import java.time.LocalDate;
import java.util.List;

/**
 * PaymentScheduleRepository provides the CRUD operations for
 * PaymentScheduleEntity. This interface extends MongoRepository and defines
 * custom query methods to find payment schedules by credit ID, find payment
 * schedules by credit card number and date range, find unpaid payment schedules
 * before a specific date, and find payment schedules by credit card number.
 */

public interface PaymentScheduleRepository extends MongoRepository<PaymentScheduleEntity, String> {

	/**
	 * Finds payment schedules by credit ID.
	 *
	 * @param creditId The credit ID to search for
	 * @return A list of PaymentScheduleEntity objects
	 */
	List<PaymentScheduleEntity> findByCreditId(String creditId);

	/**
	 * Finds payment schedules by credit card number and date range.
	 *
	 * @param creditCardNumber The credit card number to search for
	 * @param startDate        The start date of the range
	 * @param endDate          The end date of the range
	 * @return A list of PaymentScheduleEntity objects
	 */
	List<PaymentScheduleEntity> findByCreditCardNumberAndPaymentDateBetween(String creditCardNumber,
			LocalDate startDate, LocalDate endDate);

	/**
	 * Finds unpaid payment schedules before a specific date.
	 *
	 * @param date The date to search for unpaid schedules before
	 * @return A list of unpaid PaymentScheduleEntity objects
	 */
	List<PaymentScheduleEntity> findByPaidFalseAndPaymentDateBefore(LocalDate date);

	/**
	 * Finds payment schedules by credit card number.
	 *
	 * @param creditCardNumber The credit card number to search for
	 * @return A list of PaymentScheduleEntity objects
	 */
	List<PaymentScheduleEntity> findByCreditCardNumber(String creditCardNumber);
}
