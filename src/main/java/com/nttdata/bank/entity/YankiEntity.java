package com.nttdata.bank.entity;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

/**
 * YankiEntity represents the Yanki document stored in the MongoDB collection
 * "yanki". This class includes attributes such as document number, name, phone
 * number, account number, and status. It uses Lombok annotations for getters
 * and setters.
 */

@Data
@Document(collection = "yanki")
public class YankiEntity {

	@Id
	private String id;
	private String name;
	private String documentNumber;
	private String phoneNumber;
	private String accountNumber;
	private LocalDateTime createDate;
	private LocalDateTime updateDate;
	private LocalDateTime deleteDate;
	private Boolean isActive;

}
