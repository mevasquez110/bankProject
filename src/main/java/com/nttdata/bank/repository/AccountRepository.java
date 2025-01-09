package com.nttdata.bank.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.nttdata.bank.entity.AccountEntity;

public interface AccountRepository extends MongoRepository<AccountEntity, String> {

    List<AccountEntity> findByCustomerIdAndIsActiveTrue(String customerId);

    Boolean existsByAccountNumberAndIsActiveTrue(String accountNumber);

    AccountEntity findByAccountNumberAndIsActiveTrue(String accountNumber);

    List<AccountEntity> findByIsActiveTrue();

    Optional<AccountEntity> findByIdAndIsActiveTrue(String accountId);
}

