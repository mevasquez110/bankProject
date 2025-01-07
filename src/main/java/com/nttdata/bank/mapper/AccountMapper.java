package com.nttdata.bank.mapper;

import java.time.LocalDateTime;
import com.nttdata.bank.entity.AccountEntity;
import com.nttdata.bank.request.AccountRequest;
import com.nttdata.bank.response.AccountResponse;

public class AccountMapper {

    public static AccountEntity mapperToEntity(AccountRequest accountRequest) {
        AccountEntity accountEntity = new AccountEntity();

        if (accountRequest != null) {
            accountEntity.setCustomerId(accountRequest.getCustomerId());
            accountEntity.setAuthorizedSignatory(accountRequest.getAuthorizedSignatory());
            accountEntity.setAccountType(accountRequest.getAccountType());
            accountEntity.setCreateDate(LocalDateTime.now());
        }

        return accountEntity;
    }

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
