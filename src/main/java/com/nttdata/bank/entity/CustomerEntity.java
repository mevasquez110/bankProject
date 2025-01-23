package com.nttdata.bank.entity;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

/**
 * CustomerEntity represents the customer document stored in the MongoDB
 * collection "customers". This class includes attributes related to the
 * customer, such as personal and contact information, document details, and
 * status. It uses Lombok annotations for getters and setters.
 */

@Data
@Document(collection = "customers")
public class CustomerEntity {

	@Id
	private String id;
	private String fullName;
	private String companyName;
	private String email;
	private String address;
	private String phoneNumber;
	private String documentType;
	private String documentNumber;
	private String personType;
	private LocalDateTime createDate;
	private LocalDateTime updateDate;
	private LocalDateTime deleteDate;
	private Boolean isActive;

}
