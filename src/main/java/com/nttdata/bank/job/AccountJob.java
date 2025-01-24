package com.nttdata.bank.job;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.nttdata.bank.entity.TransactionEntity;
import com.nttdata.bank.repository.AccountRepository;
import com.nttdata.bank.repository.TransactionRepository;
import com.nttdata.bank.util.Constants;

/**
 * AccountJob class is responsible for handling scheduled tasks related to
 * account management. This class includes methods to check and handle inactive
 * accounts, and to review VIP accounts and charge commissions if necessary.
 */
@Component
public class AccountJob {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	/**
	 * Scheduled job to check and handle inactive accounts. This method is scheduled
	 * to run on the first day of every six months at midnight.
	 */
	@Scheduled(cron = "0 0 0 1 */6 ?")
	public void checkAndHandleInactiveAccounts() {
		LocalDateTime sixMonthsAgo = LocalDateTime.now().minus(6, ChronoUnit.MONTHS);

		accountRepository.findAllByIsActiveTrue().collectList().toFuture().join().stream()
				.filter(account -> transactionRepository.findAllByIsActiveTrue().collectList().block().stream()
						.anyMatch(transaction -> (transaction.getAccountNumberReceive()
								.equalsIgnoreCase(account.getAccountNumber())
								|| transaction.getAccountNumberWithdraws()
								.equalsIgnoreCase(account.getAccountNumber()))
								&& transaction.getCreateDate().isAfter(sixMonthsAgo)))
				.forEach(account -> {
					account.setIsActive(false);
					account.setDeleteDate(LocalDateTime.now());
					accountRepository.save(account);
				});
	}

	/**
	 * Scheduled job to review VIP accounts and charge commission if necessary. This
	 * method is scheduled to run on the first day of every month at midnight.
	 */
	@Scheduled(cron = "0 0 0 1 * ?")
	public void reviewVipAccountsAndChargeCommission() {
		LocalDateTime oneMonthAgo = LocalDateTime.now().minus(1, ChronoUnit.MONTHS);

		accountRepository.findAllByIsActiveTrue().collectList().toFuture().join().stream()
				.filter(account -> Constants.ACCOUNT_TYPE_VIP.equalsIgnoreCase(account.getAccountType()))
				.collect(Collectors.toList()).forEach(accountEntity -> {
					Double averageAmount = transactionRepository.findAllByIsActiveTrue().collectList().block().stream()
							.filter(transaction -> (accountEntity.getAccountNumber()
									.equalsIgnoreCase(transaction.getAccountNumberReceive())
									|| accountEntity.getAccountNumber()
									.equalsIgnoreCase(transaction.getAccountNumberWithdraws()))
									&& Constants.TRANSACTION_TYPE_DEPOSIT
									.equalsIgnoreCase(transaction.getTransactionType())
									&& transaction.getCreateDate().isAfter(oneMonthAgo))
							.mapToDouble(TransactionEntity::getAmount).average().orElse(0.0);

					accountEntity.setCommissionPending(averageAmount >= 1.00 ? 0.00 : Constants.COMMISSION_ADD);
					accountRepository.save(accountEntity);
				});
	}

}
