package com.example.canteen.config;

import com.example.canteen.model.User;
import com.example.canteen.repo.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Component
public class DataLoader implements ApplicationRunner {

	private final UserRepository userRepository;
	private final com.example.canteen.repo.ShopRepository shopRepository;

	public DataLoader(UserRepository userRepository, com.example.canteen.repo.ShopRepository shopRepository) {
		this.userRepository = userRepository;
		this.shopRepository = shopRepository;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		// Upsert default admin
		String adminUsername = "admin";
		String adminPassword = encoder.encode("adminpass");
		userRepository.findByUsername(adminUsername).ifPresentOrElse(user -> {
			user.setPassword(adminPassword);
			user.setRoles("ROLE_ADMIN");
			userRepository.save(user);
		}, () -> {
			userRepository.save(new User(adminUsername, adminPassword, "ROLE_ADMIN"));
		});

		// Upsert default user
		String userUsername = "user";
		String userPassword = encoder.encode("userpass");
		userRepository.findByUsername(userUsername).ifPresentOrElse(u -> {
			u.setPassword(userPassword);
			u.setRoles("ROLE_USER");
			userRepository.save(u);
		}, () -> {
			userRepository.save(new User(userUsername, userPassword, "ROLE_USER"));
		});

		// Upsert a couple of sample shops for admin dashboard
		try {
			String emptyMenu = "[]";
			String emptySubshops = "[]";

			shopRepository.findAll().stream().filter(s -> s.getName() != null && s.getName().equals("Central Canteen")).findFirst().ifPresentOrElse(s -> {
				s.setCategory("Canteen");
				s.setLat(12.8235);
				s.setLng(80.0423);
				s.setImage("https://i.ibb.co/example/canteen.jpg");
				s.setMenu(emptyMenu);
				s.setSubshops(emptySubshops);
				shopRepository.save(s);
			}, () -> {
				shopRepository.save(new com.example.canteen.model.Shop("Central Canteen", "Canteen", 12.8235, 80.0423, "https://i.ibb.co/example/canteen.jpg", emptyMenu, emptySubshops));
			});

			shopRepository.findAll().stream().filter(s -> s.getName() != null && s.getName().equals("Java Corner")).findFirst().ifPresentOrElse(s -> {
				s.setCategory("Cafe");
				s.setLat(12.8219);
				s.setLng(80.0426);
				s.setImage("https://i.ibb.co/example/java.jpg");
				s.setMenu(emptyMenu);
				s.setSubshops(emptySubshops);
				shopRepository.save(s);
			}, () -> {
				shopRepository.save(new com.example.canteen.model.Shop("Java Corner", "Cafe", 12.8219, 80.0426, "https://i.ibb.co/example/java.jpg", emptyMenu, emptySubshops));
			});
		} catch (Exception e) {
			// ignore seeding errors
		}
	}
}
