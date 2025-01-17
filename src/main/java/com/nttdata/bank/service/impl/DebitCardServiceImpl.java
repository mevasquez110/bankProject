package com.nttdata.bank.service.impl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nttdata.bank.entity.DebitCardEntity;
import com.nttdata.bank.mapper.DebitCardMapper;
import com.nttdata.bank.repository.AccountRepository;
import com.nttdata.bank.repository.DebitCardRepository;
import com.nttdata.bank.request.AssociateAccountRequest;
import com.nttdata.bank.request.DebitCardRequest;
import com.nttdata.bank.response.DebitCardResponse;
import com.nttdata.bank.service.DebitCardService;
import com.nttdata.bank.util.Constants;
import com.nttdata.bank.util.Utility;

/**
 * Implementation of the DebitCardService interface. This class provides methods
 * for creating, finding, checking balance, associating accounts, marking
 * primary accounts, and deleting debit cards.
 */
@Service
public class DebitCardServiceImpl implements DebitCardService {

	@Autowired
	private DebitCardRepository debitCardRepository;

	@Autowired
	private AccountRepository accountRepository;

	/**
	 * Creates a new debit card based on the provided request.
	 *
	 * @param debitCardRequest the debit card request containing the details for
	 *                         creating the debit card
	 * @return DebitCardResponse containing the details of the created debit card
	 */
	@Override
	public DebitCardResponse createDebitCard(DebitCardRequest debitCardRequest) {
		if (debitCardRepository.existsByDocumentNumberAndIsActiveTrue(debitCardRequest.getDocumentNumber()).block()) {
			throw new IllegalArgumentException("The client has a valid debit card");
		}
		List<String> associatedAccounts = Collections.singletonList(debitCardRequest.getPrimaryAccount());
		validateClientAccount(debitCardRequest.getDocumentNumber(), associatedAccounts);

		DebitCardEntity debitCardEntity = Optional.of(debitCardRequest).map(DebitCardMapper::mapperToEntity)
				.map(entity -> {
					entity.setDebitCardNumber(generateUniqueDebitCardNumber(debitCardRequest.getDocumentNumber()));
					entity.setAssociatedAccounts(associatedAccounts);
					entity.setCreateDate(LocalDateTime.now());
					entity.setIsBlocked(false);
					entity.setIsActive(true);
					return entity;
				}).map(debitCardRepository::save).map(future -> future.toFuture().join()).orElseThrow();

		return Optional.of(debitCardEntity).map(DebitCardMapper::mapperToResponse).orElseThrow();
	}

	private void validateClientAccount(String documentNumber, List<String> accounts) {
		Set<String> nonDuplicateAccounts = new HashSet<>(accounts);

		for (String account : nonDuplicateAccounts) {
			Boolean isClientAccount = accountRepository
					.existsByAccountNumberAndDocumentNumberAndIsActiveTrue(documentNumber, account).block();

			if (isClientAccount) {
				throw new IllegalArgumentException("The account does not exist or does not belong to the client");
			}
		}
	}

	/**
	 * Generates a unique document number.
	 *
	 * @param documentNumber The document Number
	 * @return The generated unique account number
	 */
	private String generateUniqueDebitCardNumber(String documentNumber) {
		String debitCardNumber;
		boolean exists;
		do {
			debitCardNumber = Constants.DEBIT_TYPE + Constants.BANK_CODE + documentNumber
					+ Utility.generateRandomNumber();

			exists = debitCardRepository.existsByDebitCardNumber(debitCardNumber).toFuture().join();
		} while (exists);
		return debitCardNumber;
	}

	/**
	 * Retrieves all debit cards.
	 *
	 * @return List of DebitCardResponse containing details of all debit cards
	 */
	@Override
	public List<DebitCardResponse> findAllDebitCard() {
		return debitCardRepository.findByIsActiveTrue()
				.map(DebitCardMapper::mapperToResponse).collectList().block();
	}

	/**
	 * Associates an account with a debit card based on the provided request.
	 *
	 * @param debitCardNumber         the debit card number
	 * @param associateAccountRequest the request containing details for associating
	 *                                the account
	 * @return DebitCardResponse containing the updated debit card details
	 */
	@Override
	public DebitCardResponse associateAccount(String debitCardNumber, AssociateAccountRequest associateAccountRequest) {
		DebitCardEntity debitCardEntity = debitCardRepository.findByDebitCardNumberAndIsActiveTrue(debitCardNumber)
				.blockOptional().orElseThrow(() -> {
					return new RuntimeException("Debit card not found");
				});

		List<String> accounts = associateAccountRequest.getAssociatedAccounts();
		accounts.add(associateAccountRequest.getPrimaryAccount());
		validateClientAccount(debitCardEntity.getDocumentNumber(), accounts);
		debitCardEntity.setAssociatedAccounts(associateAccountRequest.getAssociatedAccounts());
		debitCardEntity.setPrimaryAccount(associateAccountRequest.getPrimaryAccount());
		debitCardEntity.setUpdateDate(LocalDateTime.now());
		debitCardEntity = debitCardRepository.save(debitCardEntity).block();
		DebitCardResponse response = DebitCardMapper.mapperToResponse(debitCardEntity);
		return response;
	}

	/**
	 * Deletes a debit card based on the provided debit card number.
	 *
	 * @param debitCardNumber the debit card number to delete
	 */
	@Override
	public void deleteDebitCard(String debitCardNumber) {
		DebitCardEntity debitCardEntity = debitCardRepository.findByDebitCardNumberAndIsActiveTrue(debitCardNumber)
				.blockOptional().orElseThrow(() -> {
					return new RuntimeException("Debit card not found");
				});

		debitCardEntity.setDeleteDate(LocalDateTime.now());
		debitCardEntity.setIsActive(false);
		debitCardEntity = debitCardRepository.save(debitCardEntity).block();
	}

}
