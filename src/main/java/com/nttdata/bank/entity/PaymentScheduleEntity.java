package com.nttdata.bank.entity;

import java.time.LocalDate;
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
    private Double principalAmount;
    private Double interestAmount;
    private Double totalPayment;
    private String creditId;
    private Boolean paid;
}
