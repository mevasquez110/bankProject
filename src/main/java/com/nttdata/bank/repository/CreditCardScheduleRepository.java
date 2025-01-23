package com.nttdata.bank.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.nttdata.bank.entity.CreditCardScheduleEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * CreditCardScheduleRepository provides CRUD operations for
 * CreditCardScheduleEntity. This interface extends ReactiveMongoRepository and
 * defines custom query methods to find scheduled payments for credit cards.
 */
public interface CreditCardScheduleRepository extends ReactiveMongoRepository<CreditCardScheduleEntity, String> {

	/**
	 * Finds pending payments for a specific credit card that are due after the
	 * specified date.
	 *
	 * @param creditCardNumber The credit card number to search for.
	 * @param paymentDate      The payment date to search for.
	 * @return A Flux emitting the pending CreditCardScheduleEntity objects.
	 */
	Flux<CreditCardScheduleEntity> findByCreditCardNumberAndPaidFalseAndPaymentDateAfter(String creditCardNumber,
			LocalDateTime paymentDate);

	/**
	 * Finds pending payments for a specific credit card that are due on or before
	 * the specified date.
	 *
	 * @param creditCardNumber The credit card number to search for.
	 * @param currentDate      The current date.
	 * @return A Flux emitting the pending CreditCardScheduleEntity objects.
	 */
	Flux<CreditCardScheduleEntity> findByCreditCardNumberAndPaidFalseAndPaymentDateLessThanEqual(
			String creditCardNumber, LocalDate currentDate);

	/**
	 * Finds a scheduled payment for a specific credit card by its payment date.
	 *
	 * @param creditCardNumber The credit card number to search for.
	 * @param paymentDate      The payment date to search for.
	 * @return A Mono emitting the found CreditCardScheduleEntity object.
	 */
	Mono<CreditCardScheduleEntity> findByCreditCardNumberAndPaymentDate(String creditCardNumber, LocalDate paymentDate);
}
