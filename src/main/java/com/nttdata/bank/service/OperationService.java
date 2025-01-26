package com.nttdata.bank.service;

import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import com.nttdata.bank.request.AccountTransferRequest;
import com.nttdata.bank.request.DepositRequest;
import com.nttdata.bank.request.MobileTransferRequest;
import com.nttdata.bank.request.PayCreditCardRequest;
import com.nttdata.bank.request.PayCreditRequest;
import com.nttdata.bank.request.WithdrawalRequest;
import com.nttdata.bank.response.ProductResponse;
import com.nttdata.bank.response.TransactionResponse;
import reactor.core.publisher.Mono;

/**
 * TransactionService is the interface that provides methods for handling
 * transaction-related operations. This includes making deposits, making
 * withdrawals, making account transfers, making mobile transfers, paying credit
 * cards, paying credits, charging consumption, and checking transactions.
 */
public interface OperationService {

	/**
	 * Makes a deposit based on the provided deposit request.
	 *
	 * @param depositRequest The deposit request containing the details for making a
	 *                       deposit
	 * @return TransactionResponse containing the details of the completed deposit
	 */
	Mono<TransactionResponse> makeDeposit(DepositRequest depositRequest);

	/**
	 * Makes a withdrawal based on the provided withdrawal request.
	 *
	 * @param withdrawalRequest The withdrawal request containing the details for
	 *                          making a withdrawal
	 * @return TransactionResponse containing the details of the completed
	 *         withdrawal
	 */
	Mono<TransactionResponse> makeWithdrawal(WithdrawalRequest withdrawalRequest);

	/**
	 * Makes an account transfer based on the provided account transfer request.
	 *
	 * @param accountTransferRequest The account transfer request containing the
	 *                               details for making an account transfer
	 * @return TransactionResponse containing the details of the completed account
	 *         transfer
	 */
	Mono<TransactionResponse> makeAccountTransfer(AccountTransferRequest accountTransferRequest);

	/**
	 * Makes a mobile transfer based on the provided mobile transfer request.
	 *
	 * @param mobileTransferRequest The mobile transfer request containing the
	 *                              details for making a mobile transfer
	 * @return TransactionResponse containing the details of the completed mobile
	 *         transfer
	 */
	Mono<TransactionResponse> makeMobileTransfer(MobileTransferRequest mobileTransferRequest);

	/**
	 * Pays a credit card based on the provided pay credit card request.
	 *
	 * @param payCreditCardRequest The pay credit card request containing the
	 *                             details for paying the credit card
	 * @return TransactionResponse containing the details of the completed credit
	 *         card payment
	 */
	Mono<TransactionResponse> payCreditCard(PayCreditCardRequest payCreditCardRequest);

	/**
	 * Pays a credit based on the provided pay credit request.
	 *
	 * @param payCreditRequest The pay credit request containing the details for
	 *                         paying the credit
	 * @return TransactionResponse containing the details of the completed credit
	 *         payment
	 */
	Mono<TransactionResponse> payCredit(PayCreditRequest payCreditRequest);

	/**
	 * Retrieves transactions for a customer based on their document number.
	 *
	 * @param documentNumber The document number of the customer
	 * @return A list of TransactionResponse containing details of the transactions
	 */
	List<TransactionResponse> checkTransactions(String documentNumber);

	/**
	 * Retrieves products for a customer based on their document number.
	 *
	 * @param documentNumber The document number of the customer
	 * @return A list of ProductResponse containing details of the products
	 */
	List<ProductResponse> getProducts(@PathVariable String documentNumber);
}
