package com.nttdata.bank.auth;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import io.jsonwebtoken.Claims;

/**
 * Authentication token for JWT.
 */
@SuppressWarnings("serial")
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

	private Claims claims;

	/**
	 * Constructor for JwtAuthenticationToken.
	 *
	 * @param claims the JWT claims
	 */
	public JwtAuthenticationToken(Claims claims) {
		super(null);
		this.claims = claims;
		setAuthenticated(true);
	}

	/**
	 * Returns the credentials, which are not applicable in this case.
	 *
	 * @return null
	 */
	@Override
	public Object getCredentials() {
		return null;
	}

	/**
	 * Returns the principal, which is the JWT claims.
	 *
	 * @return the JWT claims
	 */
	@Override
	public Object getPrincipal() {
		return claims;
	}
}
