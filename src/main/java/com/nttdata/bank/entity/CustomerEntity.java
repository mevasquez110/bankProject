package com.nttdata.bank.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
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
