package com.nttdata.bank.mapper;

import com.nttdata.bank.entity.AccountEntity;
import com.nttdata.bank.request.AccountRequest;
import com.nttdata.bank.response.AccountResponse;

/**
 * AccountMapper provides mapping methods to convert between AccountRequest,
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

		if (accountRequest != null) {
			accountEntity.setHolderDoc(accountRequest.getHolderDoc());
			accountEntity.setAuthorizedSignatoryDoc(accountRequest.getAuthorizedSignatoryDoc());
			accountEntity.setAccountType(accountRequest.getAccountType());
		}

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

		if (accountEntity != null) {
			accountResponse.setAccountNumber(accountEntity.getAccountNumber());
			accountResponse.setMonthlyTransactionLimit(accountEntity.getMonthlyTransactionLimit());
			accountResponse.setMaintenanceCommission(accountEntity.getMaintenanceCommission());
			accountResponse.setWithdrawalDepositDate(accountEntity.getWithdrawalDepositDate());
			accountResponse.setAllowWithdrawals(accountEntity.getAllowWithdrawals());
			accountResponse.setIsBlocked(accountEntity.getIsBlocked());
		}

		return accountResponse;
	}
}
