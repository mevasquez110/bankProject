package com.nttdata.bank.repository;

import java.time.LocalDateTime;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.nttdata.bank.entity.CreditScheduleEntity;
import reactor.core.publisher.Flux;

/**
 * CreditScheduleRepository provides CRUD operations for CreditScheduleEntity.
 * This interface extends ReactiveMongoRepository and defines custom query
 * methods to find payment schedules by credit ID, find payment schedules by
 * credit card number and date range, find unpaid payment schedules before a
 * specific date, and find payment schedules by credit card number.
 */
public interface CreditScheduleRepository extends ReactiveMongoRepository<CreditScheduleEntity, String> {

	/**
	 * Finds payment schedules by credit ID, filtering for unpaid schedules with
	 * payment dates less than or equal to the specified date.
	 *
	 * @param creditId    The credit ID to search for.
	 * @param paymentDate The date to filter payment schedules.
	 * @return A Flux emitting CreditScheduleEntity objects.
	 */
	Flux<CreditScheduleEntity> findByCreditIdAndPaidFalseAndPaymentDateLessThanEqual(String creditId,
			LocalDateTime paymentDate);

	/**
	 * Finds payment schedules by credit ID, filtering for unpaid schedules with
	 * payment dates after the specified date.
	 *
	 * @param creditId    The credit ID to search for.
	 * @param paymentDate The date to filter payment schedules.
	 * @return A Flux emitting CreditScheduleEntity objects.
	 */
	Flux<CreditScheduleEntity> findByCreditIdAndPaidFalseAndPaymentDateAfter(String creditId,
			LocalDateTime paymentDate);

}
