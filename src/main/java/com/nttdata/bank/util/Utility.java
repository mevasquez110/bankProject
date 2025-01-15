package com.nttdata.bank.util;

/**
 * Utility is a utility class that provides various methods for financial
 * calculations. These methods include calculating the fixed installment amount
 * for a loan, converting an annual interest rate to a monthly interest rate,
 * and converting an annual interest rate to a daily interest rate.
 */

public class Utility {

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

	/**
	 * Generates a random number to append to the credit card number.
	 * 
	 * @return A random number as a string.
	 */
	public static String generateRandomNumber() {
		int randomNumber = (int) (Math.random() * 10000);
		return String.format("%04d", randomNumber);
	}
}
