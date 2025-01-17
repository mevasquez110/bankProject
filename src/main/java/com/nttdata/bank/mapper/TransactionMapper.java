package com.nttdata.bank.mapper;

import java.time.LocalDateTime;
import com.nttdata.bank.entity.TransactionEntity;
import com.nttdata.bank.response.TransactionResponse;

public class TransactionMapper {

	public static TransactionEntity mapperToEntity(LocalDateTime transactionDate, Double commission,
			String transactionType, Double amountSender, String accountNumberSender, String operationNumber) {
		TransactionEntity transactionEntitySender = new TransactionEntity();
		transactionEntitySender.setOperationNumber(operationNumber);
		transactionEntitySender.setAccountNumber(accountNumberSender);
		transactionEntitySender.setAmount(amountSender);
		transactionEntitySender.setCommission(commission);
		transactionEntitySender.setTransactionType(transactionType);
		transactionEntitySender.setCreateDate(transactionDate);
		transactionEntitySender.setIsActive(true);
		return transactionEntitySender;
	}

	public static TransactionResponse mapperToResponse(String operationNumber, String nameClient, String nameSender,
			String nameRecipient, String accountNumberClient, String accountNumberSender, String accountNumberRecipient,
			Double amount, Double commission, LocalDateTime transactionDate, String transactionType) {
		TransactionResponse transactionResponse = new TransactionResponse();
		transactionResponse.setOperationNumber(operationNumber);
		transactionResponse.setNameClient(nameClient);
		transactionResponse.setNameSender(nameSender);
		transactionResponse.setNameRecipient(nameRecipient);
		transactionResponse.setAccountNumberClient(accountNumberClient);
		transactionResponse.setAccountNumberSender(accountNumberSender);
		transactionResponse.setAccountNumberRecipient(accountNumberRecipient);
		transactionResponse.setAmount(amount);
		transactionResponse.setCommission(commission);
		transactionResponse.setTransactionDate(transactionDate);
		transactionResponse.setTransactionType(transactionType);
		return transactionResponse;
	}

}
