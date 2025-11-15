package com.moviesPlatform.backend.config;

import com.moviesPlatform.backend.user.User;
import com.moviesPlatform.backend.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {
    @Bean
    public CommandLineRunner initUsers(UserRepository userRepository,
                                       PasswordEncoder passwordEncoder) {
        return args -> {
            if(!userRepository.existsByUsername("admin")) {
                User admin = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("adminpass"))
                        .role("ADMIN")
                        .enabled(true)
                        .build();
                userRepository.save(admin);
            }

            if(!userRepository.existsByUsername("user")) {
                User user = User.builder()
                        .username("user")
                        .password(passwordEncoder.encode("userpass"))
                        .role("USER")
                        .enabled(true)
                        .build();
                userRepository.save(user);
            }
        };
    }
}
