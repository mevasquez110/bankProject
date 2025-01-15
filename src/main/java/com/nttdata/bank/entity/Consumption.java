package com.nttdata.bank.entity;

import java.time.LocalDate;
import lombok.Data;

@Data
public class Consumption {

	private Integer numberOfInstallments;
	private Double amount;
	private String productOrServiceName;
	private LocalDate consumptionDate;

}
