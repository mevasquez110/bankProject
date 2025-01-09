package com.nttdata.bank.mapper;

import com.nttdata.bank.entity.AccountEntity;
import com.nttdata.bank.request.AccountRequest;
import com.nttdata.bank.response.AccountResponse;

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
            accountEntity.setCustomerId(accountRequest.getCustomerId());
            accountEntity.setAuthorizedSignatory(accountRequest.getAuthorizedSignatory());
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
            accountResponse.setCreateDate(accountEntity.getCreateDate());
        }

        return accountResponse;
    }
}
