package com.nttdata.bank.mapper;

import java.time.LocalDateTime;
import com.nttdata.bank.entity.TransactionEntity;
import com.nttdata.bank.response.TransactionResponse;

/**
 * TransactionMapper provides methods to convert between TransactionEntity and
 * TransactionResponse objects, as well as mapping parameters to a new
 * TransactionEntity. This class includes methods for creating a
 * TransactionEntity from given parameters and for converting a
 * TransactionEntity to a TransactionResponse.
 */

public class TransactionMapper {

	/**
	 * Maps given parameters to a TransactionEntity object.
	 *
	 * @param transactionDate        The date of the transaction
	 * @param commission             The commission amount
	 * @param transactionType        The type of transaction
	 * @param amount                 The transaction amount
	 * @param accountNumberReceive   The account number that receives the
	 *                               transaction
	 * @param operationNumber        The operation number of the transaction
	 * @param creditId               The credit ID involved in the transaction
	 * @param accountNumberWithdraws The account number that withdraws the
	 *                               transaction
	 * @param creditCard             The credit card number involved in the
	 *                               transaction
	 * @param nameWithdraws          The name associated with the withdrawing
	 *                               account
	 * @param nameReceive            The name associated with the receiving account
	 * @return The mapped TransactionEntity
	 */
	public static TransactionEntity mapperToEntity(LocalDateTime transactionDate, Double commission,
			String transactionType, Double amount, String accountNumberReceive, String operationNumber, String creditId,
			String accountNumberWithdraws, String creditCardNumber, String nameWithdraws, String nameReceive) {
		TransactionEntity transactionEntityWithdraws = new TransactionEntity();
		transactionEntityWithdraws.setOperationNumber(operationNumber);
		transactionEntityWithdraws.setAccountNumberWithdraws(accountNumberWithdraws);
		transactionEntityWithdraws.setAccountNumberReceive(accountNumberReceive);
		transactionEntityWithdraws.setNameReceive(nameReceive);
		transactionEntityWithdraws.setNameWithdraws(nameWithdraws);
		transactionEntityWithdraws.setCreditId(creditId);
		transactionEntityWithdraws.setCreditCardNumber(creditCardNumber);
		transactionEntityWithdraws.setAmount(amount);
		transactionEntityWithdraws.setCommission(commission);
		transactionEntityWithdraws.setTransactionType(transactionType);
		transactionEntityWithdraws.setCreateDate(transactionDate);
		transactionEntityWithdraws.setIsActive(true);
		return transactionEntityWithdraws;
	}

	/**
	 * Maps a TransactionEntity object to a TransactionResponse object.
	 *
	 * @param transactionEntity The transaction entity to map
	 * @return The mapped TransactionResponse
	 */
	public static TransactionResponse mapperToResponse(TransactionEntity transactionEntity) {
		TransactionResponse transactionResponse = new TransactionResponse();
		transactionResponse.setOperationNumber(transactionEntity.getOperationNumber());
		transactionResponse.setAccountNumberWithdraws(transactionEntity.getAccountNumberWithdraws());
		transactionResponse.setNameWithdraws(transactionEntity.getNameWithdraws());
		transactionResponse.setAccountNumberReceive(transactionEntity.getAccountNumberReceive());
		transactionResponse.setNameReceive(transactionEntity.getNameReceive());
		transactionResponse.setCreditCardNumber(transactionEntity.getCreditCardNumber());
		transactionResponse.setCreditId(transactionEntity.getCreditId());
		transactionResponse.setAmount(transactionEntity.getAmount());
		transactionResponse.setCommission(transactionEntity.getCommission());
		transactionResponse.setTransactionDate(transactionEntity.getCreateDate());
		transactionResponse.setTransactionType(transactionEntity.getTransactionType());
		return transactionResponse;
	}

}
