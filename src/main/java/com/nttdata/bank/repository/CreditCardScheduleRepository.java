package com.nttdata.bank.repository;

import java.time.LocalDate;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.nttdata.bank.entity.CreditCardScheduleEntity;
import reactor.core.publisher.Flux;

public interface CreditCardScheduleRepository extends ReactiveMongoRepository<CreditCardScheduleEntity, String> {

	Flux<CreditCardScheduleEntity> findByCreditCardNumberAndPaidFalseAndPaymentDateLessThanEqual(
			String creditCardNumber, LocalDate now);

	Flux<CreditCardScheduleEntity> findByCreditCardAndPaidFalseAndPaymentDateLessThanEqual(String creditCardNumber,
			LocalDate now);

}
