package com.project.localservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.project.localservice.helper.Role;
import com.project.localservice.model.User;
import com.project.localservice.repository.UserRepository;

@SpringBootApplication
public class LocalserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LocalserviceApplication.class, args);
	}

	@Bean
    CommandLineRunner init(UserRepository repo, PasswordEncoder encoder) {
        return args -> {
            if(repo.findByEmail("admin@gmail.com").isEmpty()) {
                User admin = new User();
                admin.setName("Admin");
                admin.setEmail("admin@gmail.com");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                admin.setEnabled(true);
                admin.setCity("Katni");
                repo.save(admin);
            }
        };
	}

}
