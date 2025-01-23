package com.nttdata.bank.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ApiResponse is a generic data transfer object representing the response
 * payload for API responses. This class includes attributes such as message,
 * status code, and data. It uses Lombok annotations for getters, setters,
 * constructors, and no-argument constructors.
 *
 * @param <T> The type of data that the ApiResponse will contain
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

	private String message;
	private Integer statusCode;
	private T data;

}
