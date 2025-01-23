package com.nttdata.bank.entity;

import java.time.LocalDate;
import lombok.Data;

/**
 * Consumption class represents an individual consumption transaction. 
 * This class includes attributes such as the number of installments, 
 * the amount spent, the name of the product or service purchased, 
 * and the date of consumption.
 */

@Data
public class Consumption {

    private Integer numberOfInstallments;
    private Double amount;
    private String productOrServiceName;
    private LocalDate consumptionDate;
}
