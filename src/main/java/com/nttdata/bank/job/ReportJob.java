package com.nttdata.bank.job;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.nttdata.bank.entity.TransactionEntity;
import com.nttdata.bank.job.bean.ProductBalance;
import com.nttdata.bank.mapper.TransactionMapper;
import com.nttdata.bank.repository.AccountRepository;
import com.nttdata.bank.repository.CreditCardRepository;
import com.nttdata.bank.repository.CreditRepository;
import com.nttdata.bank.repository.TransactionRepository;
import com.nttdata.bank.response.CustomerResponse;
import com.nttdata.bank.response.TransactionResponse;
import com.nttdata.bank.service.CustomerService;
import com.nttdata.bank.util.Constants;

@Component
public class ReportJob {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private CreditRepository creditRepository;

	@Autowired
	private CreditCardRepository creditCardRepository;

	@Autowired
	private CustomerService customerService;

	/**
	 * Scheduled method that generates a daily average balance summary for all
	 * customers. This method is triggered on the first day of every month at
	 * midnight (00:00). It retrieves all active credits, credit cards, and accounts
	 * for each customer, calculates the average balance for each product, and
	 * generates a PDF summary report.
	 * 
	 * The cron expression "0 0 0 1 * ?" indicates the method runs at midnight on
	 * the first day of every month.
	 */
	@Scheduled(cron = "0 0 0 1 * ?")
	public void generateDailyAverageBalanceSummary() {
		List<ProductBalance> products = new ArrayList<>();
		List<CustomerResponse> customers = customerService.findAllCustomers();

		customers.forEach(customerResponse -> {
			String documentNumber = customerResponse.getDocumentNumber();

			Optional.ofNullable(
					creditRepository.findAllByDocumentNumberAndIsActiveTrue(documentNumber).collectList().block())
					.ifPresent(credits -> credits.forEach(creditEntity -> {
						ProductBalance product = new ProductBalance();
						product.setCreditId(creditEntity.getId());
						product.setProductType(Constants.PRODUCT_CREDIT);

						product.setAverageBalance(getTransactions().stream()
								.filter(transaction -> creditEntity.getId()
										.equalsIgnoreCase(transaction.getCreditId())
										&& Constants.TRANSACTION_TYPE_PAY_CREDIT
										.equalsIgnoreCase(transaction.getTransactionType()))
								.mapToDouble(TransactionEntity::getAmount).average().orElse(0.0));

						products.add(product);
					}));

			Optional.ofNullable(
					creditCardRepository.findAllByDocumentNumberAndIsActiveTrue(documentNumber).collectList().block())
					.ifPresent(creditCards -> creditCards.forEach(creditCardEntity -> {
						ProductBalance product = new ProductBalance();
						product.setCreditCardNumber(creditCardEntity.getCreditCardNumber());
						product.setProductType(Constants.PRODUCT_CREDIT_CARD);

						product.setAverageBalance(getTransactions().stream()
								.filter(transaction -> creditCardEntity.getCreditCardNumber()
										.equalsIgnoreCase(transaction.getCreditId())
										&& Constants.TRANSACTION_TYPE_PAY_CREDIT_CARD
										.equalsIgnoreCase(transaction.getTransactionType()))
								.mapToDouble(TransactionEntity::getAmount).average().orElse(0.0));

						products.add(product);
					}));

			Optional.ofNullable(
					accountRepository.findByHolderDocContainingAndIsActiveTrue(documentNumber).collectList().block())
					.ifPresent(accounts -> accounts.forEach(accountEntity -> {
						ProductBalance product = new ProductBalance();
						product.setAccountNumber(accountEntity.getAccountNumber());
						product.setProductType(accountEntity.getAccountType());

						product.setAverageBalance(
								calculateAverageAmount(accountEntity.getAccountNumber(), getTransactions()));

						products.add(product);
					}));
		});

		generatePdfDailyAverageBalanceSummary(products);
	}

	/**
	 * Scheduled method that generates a monthly commission report for all
	 * customers. This method is triggered on the first day of every month at
	 * midnight (00:00). It retrieves all active credits, credit cards, and accounts
	 * for each customer, calculates the average commission for each product, and
	 * generates a PDF report.
	 * 
	 * The cron expression "0 0 0 1 1/1 ?" indicates the method runs at midnight on
	 * the first day of every month.
	 */
	@Scheduled(cron = "0 0 0 1 1/1 ?")
	public void generateCommissionReport() {
		List<ProductBalance> products = new ArrayList<>();
		List<CustomerResponse> customers = customerService.findAllCustomers();

		customers.forEach(customerResponse -> {
			String documentNumber = customerResponse.getDocumentNumber();

			Optional.ofNullable(
					creditRepository.findAllByDocumentNumberAndIsActiveTrue(documentNumber).collectList().block())
					.ifPresent(credits -> credits.forEach(creditEntity -> {
						ProductBalance product = new ProductBalance();
						product.setCreditId(creditEntity.getId());
						product.setProductType(Constants.PRODUCT_CREDIT);

						product.setAverageCommission(getTransactions().stream()
								.filter(transaction -> creditEntity.getId().
										equalsIgnoreCase(transaction.getCreditId())
										&& Constants.TRANSACTION_TYPE_PAY_CREDIT
										.equalsIgnoreCase(transaction.getTransactionType()))
								.mapToDouble(TransactionEntity::getCommission).average().orElse(0.0));

						products.add(product);
					}));

			Optional.ofNullable(
					creditCardRepository.findAllByDocumentNumberAndIsActiveTrue(documentNumber).collectList().block())
					.ifPresent(creditCards -> creditCards.forEach(creditCardEntity -> {
						ProductBalance product = new ProductBalance();
						product.setCreditCardNumber(creditCardEntity.getCreditCardNumber());
						product.setProductType(Constants.PRODUCT_CREDIT_CARD);

						product.setAverageCommission(getTransactions().stream()
								.filter(transaction -> creditCardEntity.getCreditCardNumber()
										.equalsIgnoreCase(transaction.getCreditId())
										&& Constants.TRANSACTION_TYPE_PAY_CREDIT_CARD
										.equalsIgnoreCase(transaction.getTransactionType()))
								.mapToDouble(TransactionEntity::getCommission).average().orElse(0.0));

						products.add(product);
					}));

			Optional.ofNullable(
					accountRepository.findByHolderDocContainingAndIsActiveTrue(documentNumber).collectList().block())
					.ifPresent(accounts -> accounts.forEach(accountEntity -> {
						ProductBalance product = new ProductBalance();
						product.setAccountNumber(accountEntity.getAccountNumber());
						product.setProductType(accountEntity.getAccountType());

						product.setAverageCommission(
								calculateAverageCommission(accountEntity.getAccountNumber(), getTransactions()));

						products.add(product);
					}));
		});

		generatePdfDailyAverageCommissionSummary(products);
	}

	/**
	 * Scheduled method that generates a consolidated summary report for all
	 * customers. This method is triggered on the first day of every year at noon
	 * (12:00). It retrieves all active credits, credit cards, and accounts for each
	 * customer, calculates various metrics, and generates a consolidated PDF
	 * summary report.
	 * 
	 * The cron expression "0 0 12 1 1 ?" indicates the method runs at noon on the
	 * first day of every year.
	 */
	@Scheduled(cron = "0 0 12 1 1 ?")
	public void generateConsolidatedSummary() {
		List<ProductBalance> products = new ArrayList<>();
		List<CustomerResponse> customers = customerService.findAllCustomers();

		customers.forEach(customerResponse -> {
			String documentNumber = customerResponse.getDocumentNumber();

			Optional.ofNullable(
					creditRepository.findAllByDocumentNumberAndIsActiveTrue(documentNumber).collectList().block())
					.ifPresent(credits -> credits.forEach(creditEntity -> {
						ProductBalance product = new ProductBalance();
						product.setCreditId(creditEntity.getId());
						product.setProductType(Constants.PRODUCT_CREDIT);
						product.setCreditAmount(creditEntity.getAmount());

						Stream<TransactionEntity> streamTransaction = getTransactions().stream()
								.filter(transaction -> creditEntity.getId()
										.equalsIgnoreCase(transaction.getCreditId())
										&& Constants.TRANSACTION_TYPE_PAY_CREDIT
										.equalsIgnoreCase(transaction.getTransactionType()));

						product.setTransactions(streamTransaction.map(TransactionMapper::mapperToResponse)
								.collect(Collectors.toList()));

						product.setAverageBalance(
								streamTransaction.mapToDouble(TransactionEntity::getAmount)
								.average().orElse(0.0));

						product.setAverageCommission(
								streamTransaction.mapToDouble(TransactionEntity::getCommission)
								.average().orElse(0.0));

						products.add(product);
					}));

			Optional.ofNullable(
					creditCardRepository.findAllByDocumentNumberAndIsActiveTrue(documentNumber).collectList().block())
					.ifPresent(creditCards -> creditCards.forEach(creditCardEntity -> {
						ProductBalance product = new ProductBalance();
						product.setCreditCardNumber(creditCardEntity.getCreditCardNumber());
						product.setProductType(Constants.PRODUCT_CREDIT_CARD);
						product.setCreditCardAmount(creditCardEntity.getAvailableCredit());

						Stream<TransactionEntity> streamTransaction = getTransactions().stream()
								.filter(transaction -> creditCardEntity.getId()
										.equalsIgnoreCase(transaction.getCreditCardNumber())
										&& Constants.TRANSACTION_TYPE_PAY_CREDIT_CARD
										.equalsIgnoreCase(transaction.getTransactionType()));

						product.setTransactions(streamTransaction.map(TransactionMapper::mapperToResponse)
								.collect(Collectors.toList()));

						product.setAverageBalance(
								streamTransaction.mapToDouble(TransactionEntity::getAmount)
								.average().orElse(0.0));

						product.setAverageCommission(
								streamTransaction.mapToDouble(TransactionEntity::getCommission)
								.average().orElse(0.0));

						products.add(product);
					}));

			Optional.ofNullable(
					accountRepository.findByHolderDocContainingAndIsActiveTrue(documentNumber).collectList().block())
					.ifPresent(accounts -> accounts.forEach(accountEntity -> {
						ProductBalance product = new ProductBalance();
						product.setAccountNumber(accountEntity.getAccountNumber());
						product.setProductType(accountEntity.getAccountType());
						product.setAmountBalance(accountEntity.getAmount());

						List<TransactionEntity> streamTransaction = getTransactions();

						product.setTransactions(streamTransaction.stream().map(TransactionMapper::mapperToResponse)
								.collect(Collectors.toList()));

						product.setAverageBalance(
								calculateAverageAmount(accountEntity.getAccountNumber(), streamTransaction));

						product.setAverageCommission(
								calculateAverageCommission(accountEntity.getAccountNumber(), streamTransaction));

						products.add(product);
					}));
		});

		generatePdfConsolidatedSummary(products);
	}

	/**
	 * Generates a PDF summary of the daily average balance for a list of product
	 * balances. This method creates the PDF file in the directory "C:\report". If
	 * the directory does not exist, it will be created. Only non-null values are
	 * included in the PDF.
	 *
	 * @param products a list of ProductBalance objects containing the average
	 *                 balance information
	 */
	private void generatePdfDailyAverageBalanceSummary(List<ProductBalance> products) {
		try {
			createReportDirectory();

			String dest = "C:\\report\\daily_average_balance_summary.pdf";
			PdfWriter writer = new PdfWriter(dest);
			PdfDocument pdfDoc = new PdfDocument(writer);
			Document document = new Document(pdfDoc);

			document.add(new Paragraph("Daily Average Balance Summary"));

			for (ProductBalance product : products) {
				if (product.getCreditId() != null) {
					document.add(new Paragraph("Credit ID: " + product.getCreditId()));
				}
				if (product.getCreditCardNumber() != null) {
					document.add(new Paragraph("Credit Card Number: " + product.getCreditCardNumber()));
				}
				if (product.getAccountNumber() != null) {
					document.add(new Paragraph("Account Number: " + product.getAccountNumber()));
				}
				if (product.getProductType() != null) {
					document.add(new Paragraph("Product Type: " + product.getProductType()));
				}
				if (product.getAverageBalance() != null) {
					document.add(new Paragraph("Average Balance: " + product.getAverageBalance()));
				}
				document.add(new Paragraph("----------------------------------------"));
			}

			document.close();
			System.out.println("PDF created successfully.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generates a PDF summary of the daily average commission for a list of product
	 * balances. This method creates the PDF file in the directory "C:\report". If
	 * the directory does not exist, it will be created. Only non-null values are
	 * included in the PDF.
	 *
	 * @param products a list of ProductBalance objects containing the average
	 *                 commission information
	 */
	private void generatePdfDailyAverageCommissionSummary(List<ProductBalance> products) {
		try {
			createReportDirectory();

			String dest = "C:\\report\\daily_average_commission_summary.pdf";
			PdfWriter writer = new PdfWriter(dest);
			PdfDocument pdfDoc = new PdfDocument(writer);
			Document document = new Document(pdfDoc);

			document.add(new Paragraph("Daily Average Commission Summary"));

			for (ProductBalance product : products) {
				if (product.getCreditId() != null) {
					document.add(new Paragraph("Credit ID: " + product.getCreditId()));
				}
				if (product.getCreditCardNumber() != null) {
					document.add(new Paragraph("Credit Card Number: " + product.getCreditCardNumber()));
				}
				if (product.getAccountNumber() != null) {
					document.add(new Paragraph("Account Number: " + product.getAccountNumber()));
				}
				if (product.getProductType() != null) {
					document.add(new Paragraph("Product Type: " + product.getProductType()));
				}
				if (product.getAverageCommission() != null) {
					document.add(new Paragraph("Average Commission: " + product.getAverageCommission()));
				}
				document.add(new Paragraph("----------------------------------------"));
			}

			document.close();
			System.out.println("PDF created successfully.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generates a consolidated PDF summary report for a list of product balances.
	 * This method creates the PDF file in the directory "C:\report". If the
	 * directory does not exist, it will be created. Only non-null values are
	 * included in the PDF.
	 *
	 * @param products a list of ProductBalance objects containing consolidated
	 *                 summary information
	 */
	private void generatePdfConsolidatedSummary(List<ProductBalance> products) {
		try {
			createReportDirectory();

			String dest = "C:\\report\\consolidated_summary.pdf";
			PdfWriter writer = new PdfWriter(dest);
			PdfDocument pdfDoc = new PdfDocument(writer);
			Document document = new Document(pdfDoc);

			document.add(new Paragraph("Consolidated Summary"));

			for (ProductBalance product : products) {
				if (product.getCreditId() != null) {
					document.add(new Paragraph("Credit ID: " + product.getCreditId()));
				}
				if (product.getCreditCardNumber() != null) {
					document.add(new Paragraph("Credit Card Number: " + product.getCreditCardNumber()));
				}
				if (product.getAccountNumber() != null) {
					document.add(new Paragraph("Account Number: " + product.getAccountNumber()));
				}
				if (product.getProductType() != null) {
					document.add(new Paragraph("Product Type: " + product.getProductType()));
				}
				if (product.getCreditAmount() != null) {
					document.add(new Paragraph("Credit Amount: " + product.getCreditAmount()));
				}
				if (product.getCreditCardAmount() != null) {
					document.add(new Paragraph("Credit Card Amount: " + product.getCreditCardAmount()));
				}
				if (product.getAmountBalance() != null) {
					document.add(new Paragraph("Amount Balance: " + product.getAmountBalance()));
				}
				if (product.getAverageBalance() != null) {
					document.add(new Paragraph("Average Balance: " + product.getAverageBalance()));
				}
				if (product.getAverageCommission() != null) {
					document.add(new Paragraph("Average Commission: " + product.getAverageCommission()));
				}
				if (product.getTransactions() != null && !product.getTransactions().isEmpty()) {
					document.add(new Paragraph("Transactions:"));
					for (TransactionResponse transaction : product.getTransactions()) {
						if (transaction.getOperationNumber() != null) {
							document.add(new Paragraph("Operation Number: " + transaction.getOperationNumber()));
						}
						if (transaction.getAmount() != null) {
							document.add(new Paragraph("Amount: " + transaction.getAmount()));
						}
						if (transaction.getCommission() != null) {
							document.add(new Paragraph("Commission: " + transaction.getCommission()));
						}
						if (transaction.getTransactionDate() != null) {
							document.add(new Paragraph("Transaction Date: " + transaction.getTransactionDate()));
						}
						if (transaction.getTransactionType() != null) {
							document.add(new Paragraph("Transaction Type: " + transaction.getTransactionType()));
						}
						document.add(new Paragraph("----------------------------------------"));
					}
				}
			}

			document.close();
			System.out.println("PDF created successfully.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates the report directory "C:\report" if it does not exist.
	 *
	 * @throws IOException if an I/O error occurs
	 */
	private void createReportDirectory() throws IOException {
		File directory = new File("C:\\report");
		if (!directory.exists()) {
			directory.mkdirs();
		}
	}

	/**
	 * Retrieves a list of active transactions within the last month.
	 *
	 * @return a list of active TransactionEntity objects
	 */
	private List<TransactionEntity> getTransactions() {
		return transactionRepository.findAllByIsActiveTrue().collectList().block().stream()
				.filter(transaction -> transaction.getCreateDate()
						.isAfter(LocalDateTime.now().minusMonths(1).withDayOfMonth(1).minusDays(1))
						&& transaction.getCreateDate().isBefore(LocalDateTime.now().withDayOfMonth(1)))
				.collect(Collectors.toList());
	}

	/**
	 * Calculates the average amount for a given account number from a list of
	 * transactions.
	 *
	 * @param accountNumber the account number to filter transactions
	 * @param transactions  a list of TransactionEntity objects
	 * @return the calculated average amount
	 */
	private double calculateAverageAmount(String accountNumber, List<TransactionEntity> transactions) {
		return transactions.stream()
				.filter(transaction -> accountNumber.equalsIgnoreCase(transaction.getAccountNumberReceive()))
				.mapToDouble(TransactionEntity::getAmount).sum()
				- transactions.stream()
						.filter(transaction -> accountNumber.equalsIgnoreCase(transaction.getAccountNumberWithdraws()))
						.mapToDouble(TransactionEntity::getAmount).sum();
	}

	/**
	 * Calculates the average commission for a given account number from a list of
	 * transactions.
	 *
	 * @param accountNumber the account number to filter transactions
	 * @param transactions  a list of TransactionEntity objects
	 * @return the calculated average commission
	 */
	private double calculateAverageCommission(String accountNumber, List<TransactionEntity> transactions) {
		return transactions.stream()
				.filter(transaction -> accountNumber.equalsIgnoreCase(transaction.getAccountNumberReceive()))
				.mapToDouble(TransactionEntity::getCommission).sum()
				- transactions.stream()
						.filter(transaction -> accountNumber.equalsIgnoreCase(transaction.getAccountNumberWithdraws()))
						.mapToDouble(TransactionEntity::getCommission).sum();
	}

}
