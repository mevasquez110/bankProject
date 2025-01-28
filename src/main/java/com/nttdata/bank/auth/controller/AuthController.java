package com.nttdata.bank.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bank.auth.request.AuthRequest;
import com.nttdata.bank.auth.service.AuthenticationService;

/**
 * Controller class for handling authentication-related requests.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private AuthenticationService authenticationService;

	/**
	 * Endpoint to generate a JWT token.
	 *
	 * @param authRequest the authentication request containing the username
	 * @return a ResponseEntity containing the generated token and HTTP status
	 */
	@PostMapping("/generate-token")
	public ResponseEntity<String> generateToken(@RequestBody AuthRequest authRequest) {
		String token = authenticationService.generateJwt(authRequest.getUsername());
		return new ResponseEntity<>(token, HttpStatus.OK);
	}
}
