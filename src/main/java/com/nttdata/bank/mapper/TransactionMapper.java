package com.nttdata.bank.mapper;

import com.nttdata.bank.entity.TransactionEntity;
import com.nttdata.bank.request.TransactionRequest;
import com.nttdata.bank.response.TransactionResponse;

public class TransactionMapper {

    /**
     * Maps a TransactionRequest object to a TransactionEntity object.
     *
     * @param transactionRequest The transaction request to map
     * @return The mapped transaction entity
     */
    public static TransactionEntity mapperToEntity(TransactionRequest transactionRequest) {
        TransactionEntity transactionEntity = new TransactionEntity();

        if (transactionRequest != null) {
            transactionEntity.setAccountNumber(transactionRequest.getAccountNumber());
            transactionEntity.setCreditCardNumber(transactionRequest.getCreditCardNumber());
            transactionEntity.setCreditId(transactionRequest.getCreditId());
            transactionEntity.setAmount(transactionRequest.getAmount());
        }

        return transactionEntity;
    }

    /**
     * Maps a TransactionEntity object to a TransactionResponse object.
     *
     * @param transactionEntity The transaction entity to map
     * @return The mapped transaction response
     */
    public static TransactionResponse mapperToResponse(TransactionEntity transactionEntity) {
        TransactionResponse transactionResponse = new TransactionResponse();

        if (transactionEntity != null) {
            transactionResponse.setTransactionId(transactionEntity.getId());
            transactionResponse.setAccountNumber(transactionEntity.getAccountNumber());
            transactionResponse.setCreditCardNumber(transactionEntity.getCreditCardNumber());
            transactionResponse.setCreditId(transactionEntity.getCreditId());
            transactionResponse.setAmount(transactionEntity.getAmount());
            transactionResponse.setCreateDate(transactionEntity.getCreateDate());
            transactionResponse.setTransactionType(transactionEntity.getTransactionType());
        }

        return transactionResponse;
    }
}
