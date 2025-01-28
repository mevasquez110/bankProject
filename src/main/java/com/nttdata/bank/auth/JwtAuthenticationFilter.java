package com.nttdata.bank.auth;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

/**
 * Filter for handling JWT authentication in HTTP requests.
 */
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

	private final String secretKey;

	public JwtAuthenticationFilter(AuthenticationManager authenticationManager, String secretKey) {
		super(authenticationManager);
		this.secretKey = secretKey;
	}

	/**
	 * Filters HTTP requests to validate JWT tokens.
	 *
	 * @param request  the HTTP request
	 * @param response the HTTP response
	 * @param chain    the filter chain
	 * @throws IOException      if an input or output error occurs
	 * @throws ServletException if a servlet error occurs
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
			FilterChain chain)
			throws IOException, ServletException {
		String header = request.getHeader("Authorization");

		if (header == null || !header.startsWith("Bearer ")) {
			chain.doFilter(request, response);
			return;
		}

		String token = header.replace("Bearer ", "");
		Claims claims = Jwts.parser()
				.setSigningKey(secretKey)
				.parseClaimsJws(token)
				.getBody();

		SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(claims));
		chain.doFilter(request, response);
	}
}
