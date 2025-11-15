package com.moviesPlatform.backend.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    /* Will be used in UserDetailsService (Spring Security) */
    Optional<User> findByUsername(String username);

    /* Validation when creating new users. */
    boolean existsByUsername(String username);
}
