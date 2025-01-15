package com.nttdata.bank.util;

/**
 * Constants is a utility class that contains various constant values used
 * throughout the application. These constants include account types, document
 * types, person types, bank code, and account type codes.
 */

public class Constants {

	// Constants for different types of accounts
	public static final String ACCOUNT_TYPE_SAVINGS = "Ahorro";
	public static final String ACCOUNT_TYPE_CHECKING = "Cuenta corriente";
	public static final String ACCOUNT_TYPE_FIXED_TERM = "Plazo fijo";
	public static final String ACCOUNT_TYPE_VIP = "VIP";
	public static final String ACCOUNT_TYPE_PYME = "PYME";
	public static final String ACCOUNT_TYPE_YANKI = "Yanki";
	
	// Constants for different types of documents
	public static final String DOCUMENT_TYPE_DNI = "DNI";
	public static final String DOCUMENT_TYPE_CE = "CE";
	public static final String DOCUMENT_TYPE_RUC = "RUC";

	// Constants for different types of persons
	public static final String PERSON_TYPE_PERSONAL = "personal";
	public static final String PERSON_TYPE_BUSINESS = "empresarial";

	// Bank code and account type codes
	public static final String BANK_CODE = "002";
	public static final String ACCOUNT_TYPE_CODE_SAVINGS = "1";
	public static final String ACCOUNT_TYPE_CODE_CHECKING = "2";
	public static final String ACCOUNT_TYPE_CODE_FIXED_TERM = "3";
	public static final String ACCOUNT_TYPE_CODE_VIP = "4";
	public static final String ACCOUNT_TYPE_CODE_PYME = "5";
	public static final String ACCOUNT_TYPE_CODE_YANKI = "6";
	public static final String CREDIT_TYPE = "7";
	public static final String DEBIT_TYPE = "8";
	
	public static final String CURRENCY_SOL = "Soles";
	
	public static final String TRANSACTION_TYPE_DEPOSIT = "DEPOSITO";
	public static final String TRANSACTION_TYPE_WITHDRAWAL = "RETIRO";
	public static final String TRANSACTION_TYPE_BANK_TRANSFER = "TRANSFERENCIA BANCARIA";
	public static final String TRANSACTION_TYPE_MOBILE_TRANSFER = "TRANSFERENCIA MOVIL";
	public static final String TRANSACTION_TYPE_PAY_CREDIT_CARD = "PAGO DE TARJETA";
	public static final String TRANSACTION_TYPE_PAY_CREDIT = "PAGO DE CREDITO";
	public static final String TRANSACTION_TYPE_CONSUMPTION = "CONSUMO DE TARJETA";

}
