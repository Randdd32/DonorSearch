package com.github.randdd32.donor_search_backend.service.auth;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import com.github.randdd32.donor_search_backend.model.auth.UserEntity;
import com.github.randdd32.donor_search_backend.repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserEntity validateAndGetUser(String username, String rawPassword) {
        UserEntity user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        return user;
    }

    public void validatePasswordStrength(String rawPassword) {
        if (rawPassword == null || !rawPassword.matches(Constants.PASSWORD_PATTERN)) {
            throw new IllegalArgumentException("Password does not meet security requirements. " +
                    "It must contain 8-60 characters, uppercase and lowercase letters, a digit, and a special character.");
        }
    }
}
