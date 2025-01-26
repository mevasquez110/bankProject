package com.nttdata.bank.mapper;

import java.time.LocalDateTime;
import com.nttdata.bank.entity.AccountEntity;
import com.nttdata.bank.request.AccountRequest;
import com.nttdata.bank.response.AccountResponse;
import com.nttdata.bank.response.BalanceResponse;
import com.nttdata.bank.util.Constants;

/**
 * AccountMapper provides methods to convert between AccountRequest,
 * AccountEntity, and AccountResponse objects. This class includes methods to
 * map an AccountRequest to an AccountEntity and to map an AccountEntity to an
 * AccountResponse.
 */

public class AccountMapper {

	/**
	 * Maps an AccountRequest object to an AccountEntity object.
	 *
	 * @param accountRequest The account request to map
	 * @return The mapped account entity
	 */
	public static AccountEntity mapperToEntity(AccountRequest accountRequest) {
		AccountEntity accountEntity = new AccountEntity();
		accountEntity.setHolderDoc(accountRequest.getHolderDoc());
		accountEntity.setAuthorizedSignatoryDoc(accountRequest.getAuthorizedSignatoryDoc());
		accountEntity.setAccountType(accountRequest.getAccountType());
		accountEntity.setCurrency(Constants.CURRENCY_SOL);
		accountEntity.setCommissionPending(0.00);
		accountEntity.setCreateDate(LocalDateTime.now());
		accountEntity.setIsActive(true);
		return accountEntity;
	}

	/**
	 * Maps an AccountEntity object to an AccountResponse object.
	 *
	 * @param accountEntity The account entity to map
	 * @return The mapped account response
	 */
	public static AccountResponse mapperToResponse(AccountEntity accountEntity) {
		AccountResponse accountResponse = new AccountResponse();
		accountResponse.setAccountNumber(accountEntity.getAccountNumber());
		accountResponse.setMonthlyTransactionLimit(accountEntity.getMonthlyTransactionLimit());
		accountResponse.setMaintenanceCommission(accountEntity.getMaintenanceCommission());
		accountResponse.setWithdrawalDepositDate(accountEntity.getWithdrawalDepositDate());
		accountResponse.setAmount(accountEntity.getAmount());
		accountResponse.setCurrency(accountEntity.getCurrency());
		return accountResponse;
	}

	/**
	 * Maps an AccountEntity object to a BalanceResponse object.
	 *
	 * @param accountEntity The account entity to map
	 * @return The mapped balance response
	 */
	public static BalanceResponse mapperToBalanceResponse(AccountEntity accountEntity) {
		BalanceResponse balanceResponse = new BalanceResponse();
		balanceResponse.setAccountNumber(accountEntity.getAccountNumber());
		balanceResponse.setAmount(accountEntity.getAmount());
		return balanceResponse;
	}
}
