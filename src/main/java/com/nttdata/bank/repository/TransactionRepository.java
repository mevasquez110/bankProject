package com.nttdata.bank.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.nttdata.bank.entity.TransactionEntity;

public interface TransactionRepository extends MongoRepository<TransactionEntity, String> {
}