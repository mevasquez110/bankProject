package com.nttdata.bank.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.nttdata.bank.entity.DebitCardEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DebitCardRepository extends ReactiveMongoRepository<DebitCardEntity, String> {

	Mono<Boolean> existsByDocumentNumberAndIsActiveTrue(String documentNumber);

	Mono<Boolean> existsByDebitCardNumberAndIsActiveTrue(String debitCardNumber);

	Flux<DebitCardEntity> findByIsActiveTrue();

	Mono<DebitCardEntity> findByDebitCardNumberAndIsActiveTrue(String debitCardNumber);
	
	Mono<Boolean> existsByPrimaryAccountAndIsActiveTrue(String primaryAccount);

	Mono<Boolean> existsByDebitCardNumber(String debitCardNumber);

}
