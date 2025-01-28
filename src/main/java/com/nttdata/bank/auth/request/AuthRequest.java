package com.nttdata.bank.auth.request;

import lombok.Data;

/**
 * Request class for authentication containing the username.
 */
@Data
public class AuthRequest {

	private String username;

}
