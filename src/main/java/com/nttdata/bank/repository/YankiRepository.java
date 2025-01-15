package com.nttdata.bank.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.nttdata.bank.entity.YankiEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * YankiRepository is an interface that extends ReactiveMongoRepository for
 * performing CRUD operations on YankiEntity documents in MongoDB.
 * 
 * <p>
 * This repository provides reactive support for accessing YankiEntity
 * documents by their ID and other attributes, leveraging Spring Data MongoDB.
 */
public interface YankiRepository extends ReactiveMongoRepository<YankiEntity, String> {

    /**
     * Checks if a Yanki entity exists by document number and is active.
     *
     * @param documentNumber the document number to check for existence
     * @return Mono emitting true if a Yanki entity with the given document number exists and is active, false otherwise
     */
    Mono<Boolean> existsByDocumentNumberAndIsActiveTrue(String documentNumber);
    
    /**
     * Checks if a Yanki entity exists by account number and is active.
     *
     * @param accountNumber the account number to check for existence
     * @return Mono emitting true if a Yanki entity with the given account number exists and is active, false otherwise
     */
    Mono<Boolean> existsByAccountNumberAndIsActiveTrue(String accountNumber);

    /**
     * Finds a Yanki entity by phone number and is active.
     *
     * @param phoneNumber the phone number to search for
     * @return Mono emitting the Yanki entity if found and is active, empty otherwise
     */
    Mono<YankiEntity> findByPhoneNumberAndIsActiveTrue(String phoneNumber);

    /**
     * Finds all Yanki entities that are active.
     *
     * @return Flux emitting all active Yanki entities
     */
    Flux<YankiEntity> findAllByIsActiveTrue();
}
