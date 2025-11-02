package com.example.canteen.web;

import com.example.canteen.repo.UserRepository;
import com.example.canteen.service.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final UserRepository userRepository;
	private final TokenService tokenService;
	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	public AuthController(UserRepository userRepository, TokenService tokenService) {
		this.userRepository = userRepository;
		this.tokenService = tokenService;
	}

	public static class LoginRequest {
		public String username;
		public String password;
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest req) {
		if (req == null || req.username == null || req.password == null) {
			return ResponseEntity.badRequest().body(Map.of("error", "missing_credentials"));
		}
		return userRepository.findByUsername(req.username)
				.map(user -> {
					if (passwordEncoder.matches(req.password, user.getPassword())) {
						String token = tokenService.createToken(user.getUsername());
						// derive simple role name for frontend (ADMIN or USER)
						String role = "USER";
						String rolesStr = user.getRoles();
						if (rolesStr != null && rolesStr.toUpperCase().contains("ADMIN")) role = "ADMIN";
						else if (rolesStr != null && rolesStr.toUpperCase().contains("USER")) role = "USER";
						return ResponseEntity.ok(Map.of("token", token, "username", user.getUsername(), "role", role));
					} else {
						return ResponseEntity.status(401).body(Map.of("error", "invalid_credentials"));
					}
				})
				.orElseGet(() -> ResponseEntity.status(401).body(Map.of("error", "invalid_credentials")));
	}
}
