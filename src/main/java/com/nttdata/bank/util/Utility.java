package com.nttdata.bank.util;

/**
 * Utility is a utility class that provides various methods for financial
 * calculations. These methods include calculating the fixed installment amount
 * for a loan, converting an annual interest rate to a monthly interest rate,
 * and converting an annual interest rate to a daily interest rate.
 */

public class Utility {

	/**
	 * Calculates the fixed installment amount for a loan based on the annuity
	 * formula.
	 *
	 * @param amount               The principal loan amount
	 * @param monthlyInterestRate  The monthly interest rate
	 * @param numberOfInstallments The number of installments
	 * @return The fixed installment amount
	 */
	public static Double calculateInstallmentAmount(Double amount, Double monthlyInterestRate,
			Integer numberOfInstallments) {
		return amount * monthlyInterestRate * Math.pow(1 + monthlyInterestRate, numberOfInstallments)
				/ (Math.pow(1 + monthlyInterestRate, numberOfInstallments) - 1);
	}

	/**
	 * Converts an annual interest rate to a monthly interest rate.
	 *
	 * @param annualInterestRate The annual interest rate
	 * @return The monthly interest rate
	 */
	public static Double getMonthlyInterestRate(Double annualInterestRate) {
		return annualInterestRate / 12 / 100;
	}

	/**
	 * Converts an annual interest rate to a daily interest rate.
	 *
	 * @param annualInterestRate The annual interest rate
	 * @return The daily interest rate
	 */
	public static Double getDailyInterestRate(Double annualInterestRate) {
		return annualInterestRate / 365 / 100;
	}
}
