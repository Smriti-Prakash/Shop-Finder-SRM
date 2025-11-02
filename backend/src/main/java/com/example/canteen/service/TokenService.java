package com.example.canteen.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Service
public class TokenService {

	private final String secret;

	public TokenService(@Value("${app.auth.secret:dev-secret}") String secret) {
		this.secret = secret;
	}

	/**
	 * Create a very small opaque token for dev use. This is not a secure JWT.
	 * For production, replace with a signed JWT or other secure session mechanism.
	 */
	public String createToken(String username) {
		String payload = username + ":" + UUID.randomUUID();
		byte[] raw = (payload + ":" + secret).getBytes(StandardCharsets.UTF_8);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(raw);
	}
}
