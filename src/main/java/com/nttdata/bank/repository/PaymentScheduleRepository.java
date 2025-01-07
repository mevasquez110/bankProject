package com.nttdata.bank.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.nttdata.bank.entity.PaymentScheduleEntity;
import java.util.List;

public interface PaymentScheduleRepository extends MongoRepository<PaymentScheduleEntity, String> {

	List<PaymentScheduleEntity> findByCreditId(String creditId);
}
