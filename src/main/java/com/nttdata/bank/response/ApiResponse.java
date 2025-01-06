package com.nttdata.bank.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor 
@NoArgsConstructor
public class ApiResponse<T>  {

	private String message;
    private int statusCode;
    private T data;
}
