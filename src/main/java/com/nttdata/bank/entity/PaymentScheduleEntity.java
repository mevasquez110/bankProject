package com.nttdata.bank.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "payment_schedules")
public class PaymentScheduleEntity {
	
    @Id
    private String id;
    private LocalDate paymentDate;
    private Double debtAmount;
    private Double sharePayment;
    private String creditId;
    private String creditCardNumber;
    private Boolean paid;
	private LocalDateTime createDate;
	private LocalDateTime updateDate;
	private LocalDateTime deleteDate;
	private Boolean isActive;
}

