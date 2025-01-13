package com.nttdata.bank.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.nttdata.bank.entity.YankiEntity;

/**
 * * YankiRepository is an interface that extends ReactiveMongoRepository for
 * performing * CRUD operations on YankiEntity documents in MongoDB. *
 * <p>
 * * This repository provides reactive support for accessing YankiEntity
 * documents by * their ID and other attributes, leveraging Spring Data MongoDB.
 */
public interface YankiRepository extends ReactiveMongoRepository<YankiEntity, String> {

}
