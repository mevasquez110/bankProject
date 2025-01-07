package com.nttdata.bank.util;

public class Utility {

	public static Double calculateInstallmentAmount(Double amount, Double monthlyInterestRate,
			Integer numberOfInstallments) {
		return amount * monthlyInterestRate * Math.pow(1 + monthlyInterestRate, numberOfInstallments)
				/ (Math.pow(1 + monthlyInterestRate, numberOfInstallments) - 1);
	}

	public static Double getMonthlyInterestRate(Double annualInterestRate) {
		return annualInterestRate / 12 / 100;
	}

	public static Double getDailyInterestRate(Double annualInterestRate) {
		return annualInterestRate / 365 / 100;
	}
}
