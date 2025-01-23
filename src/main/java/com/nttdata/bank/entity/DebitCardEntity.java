package com.nttdata.bank.entity;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

/**
 * DebitCardEntity represents the debit card document stored in the MongoDB
 * collection "debit_cards". This class includes attributes related to the debit
 * card, such as card details, associated accounts, status, and timestamps. It
 * uses Lombok annotations for getters and setters.
 */

@Data
@Document(collection = "debit_cards")
public class DebitCardEntity {

	@Id
	private String debitCardId;
	private String debitCardNumber;
	private String documentNumber;
	private List<String> associatedAccounts;
	private String primaryAccount;
	private LocalDateTime createDate;
	private LocalDateTime updateDate;
	private LocalDateTime deleteDate;
	private Boolean isBlocked;
	private Boolean isActive;
}
