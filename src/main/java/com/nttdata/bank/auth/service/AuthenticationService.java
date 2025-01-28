package com.nttdata.bank.auth.service;

/**
 * Service interface for handling authentication-related operations.
 */
public interface AuthenticationService {

	/**
	 * Generates a JWT token for the given username.
	 *
	 * @param username the username for which to generate the token
	 * @return the generated JWT token
	 */
	public String generateJwt(String username);

	/**
	 * Validates the given JWT token.
	 *
	 * @param token the JWT token to validate
	 * @return true if the token is valid, false otherwise
	 */
	public boolean validateJwt(String token);

}
