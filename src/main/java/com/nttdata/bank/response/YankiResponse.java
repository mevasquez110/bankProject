package com.nttdata.bank.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * YankiResponse is a data transfer object that represents the response 
 * payload for a Yanki entity. This class includes attributes such as name 
 * and phone number. It uses Lombok annotations for getters and setters and 
 * Jackson annotations for JSON inclusion.
 */

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class YankiResponse {

    private String name;
    private String phoneNumber;
}
