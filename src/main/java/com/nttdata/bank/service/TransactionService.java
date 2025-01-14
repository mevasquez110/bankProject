package com.nttdata.bank.service;

import java.util.List;
import com.nttdata.bank.request.AccountTransferRequest;
import com.nttdata.bank.request.ConsumptionRequest;
import com.nttdata.bank.request.DepositRequest;
import com.nttdata.bank.request.MobileTransferRequest;
import com.nttdata.bank.request.PayCreditCardRequest;
import com.nttdata.bank.request.PayCreditRequest;
import com.nttdata.bank.request.WithdrawalRequest;
import com.nttdata.bank.response.TransactionResponse;

/**
 * TransactionService is the interface that provides methods for handling 
 * transaction-related operations. This includes making deposits, making 
 * withdrawals, making account transfers, making mobile transfers, paying 
 * credit cards, paying credits, charging consumption, and checking transactions.
 */
public interface TransactionService {
    
    /**
     * Makes a deposit based on the provided deposit request.
     *
     * @param depositRequest the deposit request containing the details for making a deposit
     * @return TransactionResponse containing the details of the completed deposit
     */
    TransactionResponse makeDeposit(DepositRequest depositRequest);

    /**
     * Makes a withdrawal based on the provided withdrawal request.
     *
     * @param withdrawalRequest the withdrawal request containing the details for making a withdrawal
     * @return TransactionResponse containing the details of the completed withdrawal
     */
    TransactionResponse makeWithdrawal(WithdrawalRequest withdrawalRequest);

    /**
     * Makes an account transfer based on the provided account transfer request.
     *
     * @param accountTransferRequest the account transfer request containing the details for making an account transfer
     * @return TransactionResponse containing the details of the completed account transfer
     */
    TransactionResponse makeAccountTransfer(AccountTransferRequest accountTransferRequest);

    /**
     * Makes a mobile transfer based on the provided mobile transfer request.
     *
     * @param mobileTransferRequest the mobile transfer request containing the details for making a mobile transfer
     * @return TransactionResponse containing the details of the completed mobile transfer
     */
    TransactionResponse makeMobileTransfer(MobileTransferRequest mobileTransferRequest);

    /**
     * Pays a credit card based on the provided pay credit card request.
     *
     * @param payCreditCardRequest the pay credit card request containing the details for paying the credit card
     * @return TransactionResponse containing the details of the completed credit card payment
     */
    TransactionResponse payCreditCard(PayCreditCardRequest payCreditCardRequest);

    /**
     * Pays a credit based on the provided pay credit request.
     *
     * @param payCreditRequest the pay credit request containing the details for paying the credit
     * @return TransactionResponse containing the details of the completed credit payment
     */
    TransactionResponse payCredit(PayCreditRequest payCreditRequest);

    /**
     * Charges a consumption based on the provided consumption request.
     *
     * @param consumptionRequest the consumption request containing the details for charging the consumption
     * @return TransactionResponse containing the details of the completed consumption charge
     */
    TransactionResponse chargeConsumption(ConsumptionRequest consumptionRequest);

    /**
     * Checks the transactions for the provided account ID.
     *
     * @param accountId the account ID for which to check the transactions
     * @return List of TransactionResponse containing the details of the transactions
     */
    List<TransactionResponse> checkTransactions(Integer accountId);
}
