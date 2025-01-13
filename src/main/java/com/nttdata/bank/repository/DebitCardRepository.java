package com.nttdata.bank.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.nttdata.bank.entity.DebitCardEntity;

public interface DebitCardRepository extends ReactiveMongoRepository<DebitCardEntity, String> {

}
