package com.nttdata.bank.auth.service.impl;

import org.springframework.stereotype.Service;
import com.nttdata.bank.auth.service.AuthenticationService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;

/**
 * Service implementation class for handling authentication.
 */
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

	private static final String SECRET_KEY = "secretKey";

	/**
	 * Generates a JWT token for the given username.
	 *
	 * @param username the username for which to generate the token
	 * @return the generated JWT token
	 */
	public String generateJwt(String username) {
		return Jwts.builder()
				.setSubject(username)
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + 86400000))
				.signWith(SignatureAlgorithm.HS512, SECRET_KEY)
				.compact();
	}

	/**
	 * Validates the given JWT token.
	 *
	 * @param token the JWT token to validate
	 * @return true if the token is valid, false otherwise
	 */
	public boolean validateJwt(String token) {
		try {
			Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
