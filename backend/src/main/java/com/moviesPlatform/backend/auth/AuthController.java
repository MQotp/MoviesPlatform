package com.moviesPlatform.backend.auth;

import com.moviesPlatform.backend.security.CustomUserDetails;
import com.moviesPlatform.backend.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.token.TokenService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
            String token = jwtTokenProvider.generateToken(authentication);
            Date expiresAt = jwtTokenProvider.getExpirationDate();

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            LoginResponse response = new LoginResponse(
                    token,
                    userDetails.getUsername(),
                    userDetails.getUser().getRole(),
                    expiresAt
            );

            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            // Wrong username or password
            return ResponseEntity.status(401).build();
        } catch (AuthenticationException e) {
            // Any other authentication error
            return ResponseEntity.status(401).build();
        }
    }
}
