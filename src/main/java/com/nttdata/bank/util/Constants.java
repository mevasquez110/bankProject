package com.nttdata.bank.util;

/**
 * Constants is a utility class that contains various constant values used
 * throughout the application. These constants include account types, document
 * types, person types, bank code, and account type codes.
 */

public class Constants {

	// Constants for different types of accounts
	public static final String ACCOUNT_TYPE_SAVINGS = "Cuenta de Ahorro";
	public static final String ACCOUNT_TYPE_CHECKING = "Cuenta corriente";
	public static final String ACCOUNT_TYPE_FIXED_TERM = "Cuenta de Plazo fijo";
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

	public static final String TRANSACTION_TYPE_DEPOSIT = "Deposito";
	public static final String TRANSACTION_TYPE_WITHDRAWAL = "Retiro";
	public static final String TRANSACTION_TYPE_BANK_TRANSFER = "Transferencia bancaria";
	public static final String TRANSACTION_TYPE_MOBILE_TRANSFER = "Transferencia movil";
	public static final String TRANSACTION_TYPE_PAY_CREDIT_CARD = "Pago de tarjeta";
	public static final String TRANSACTION_TYPE_PAY_CREDIT = "Pago de credito";
	public static final String TRANSACTION_TYPE_CONSUMPTION = "Consumo de tarjeta";

	public static final String PRODUCT_CREDIT = "Credito";
	public static final String PRODUCT_CREDIT_CARD = "Tarjeta de credito";

	public static final Integer TRANSACTIONAL_LIMIT = 10;
	public static final Double COMMISSION_ADD = 1.99;

}
