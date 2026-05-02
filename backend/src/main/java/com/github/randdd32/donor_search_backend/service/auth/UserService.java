package com.github.randdd32.donor_search_backend.service.auth;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import com.github.randdd32.donor_search_backend.core.error.NotFoundException;
import com.github.randdd32.donor_search_backend.model.auth.UserEntity;
import com.github.randdd32.donor_search_backend.model.enums.UserRole;
import com.github.randdd32.donor_search_backend.repository.auth.UserRepository;
import com.github.randdd32.donor_search_backend.repository.specification.UserSpecification;
import com.github.randdd32.donor_search_backend.service.AbstractReadService;
import com.github.randdd32.donor_search_backend.web.dto.auth.UserCreateDto;
import com.github.randdd32.donor_search_backend.web.dto.auth.UserUpdateDto;
import com.github.randdd32.donor_search_backend.web.dto.filter.UserFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserService extends AbstractReadService<UserEntity, UserRepository> {
    private final PasswordEncoder passwordEncoder;
    private final RefreshSessionService refreshSessionService;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder, RefreshSessionService refreshSessionService) {
        super(repository, UserEntity.class);
        this.passwordEncoder = passwordEncoder;
        this.refreshSessionService = refreshSessionService;
    }

    @Transactional
    public void initSuperAdmin() {
        if (!repository.existsByRole(UserRole.SUPERADMIN)) {
            log.info("No SUPERADMIN found. Creating default superadmin account...");
            UserEntity admin = new UserEntity();
            admin.setUsername("Admin");
            admin.setPasswordHash(passwordEncoder.encode("Admin12!"));
            admin.setRole(UserRole.SUPERADMIN);
            repository.save(admin);
            log.info("Default SUPERADMIN created (username: Admin, password: Admin12!). Please change password in production!");
        }
    }

    @Transactional(readOnly = true)
    public UserEntity validateAndGetUser(String username, String rawPassword) {
        UserEntity user = getByUsername(username);

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        return user;
    }

    @Transactional(readOnly = true)
    public UserEntity getByUsername(String username) {
        return repository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new NotFoundException(UserEntity.class, "username", username));
    }

    @Transactional(readOnly = true)
    public Page<UserEntity> getAll(UserFilter filter, Pageable pageable) {
        Specification<UserEntity> spec = UserSpecification.withFilters(filter);
        return repository.findAll(spec, pageable);
    }

    @Transactional
    public UserEntity createUser(UserCreateDto dto, UserRole currentUserRole) {
        if (dto.username() == null || dto.username().isBlank()) {
            throw new IllegalArgumentException("Username must not be null or empty");
        }
        if (dto.role() == null) {
            throw new IllegalArgumentException("Role must not be null");
        }
        if (repository.findByUsernameIgnoreCase(dto.username()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        validatePasswordStrength(dto.password());
        checkRoleHierarchy(currentUserRole, dto.role(), "create");

        UserEntity user = new UserEntity();
        user.setUsername(dto.username().trim());
        user.setPasswordHash(passwordEncoder.encode(dto.password()));
        user.setRole(dto.role());

        return repository.save(user);
    }

    @Transactional
    public UserEntity updateUser(Long id, UserUpdateDto dto, UserRole currentUserRole, String currentUsername) {
        if (dto.role() == null) {
            throw new IllegalArgumentException("Role must not be null");
        }

        UserEntity user = getById(id);
        boolean isSelf = user.getUsername().equalsIgnoreCase(currentUsername);

        if (!isSelf) {
            checkRoleHierarchy(currentUserRole, user.getRole(), "update");
            checkRoleHierarchy(currentUserRole, dto.role(), "assign");
        } else {
            if (user.getRole() != dto.role()) {
                throw new IllegalArgumentException("You cannot change your own role");
            }
        }

        user.setRole(dto.role());

        if (dto.password() != null && !dto.password().isBlank()) {
            validatePasswordStrength(dto.password());
            user.setPasswordHash(passwordEncoder.encode(dto.password()));
            refreshSessionService.revokeAllUserSessions(user.getId());
        }

        return repository.save(user);
    }

    @Transactional
    public void deleteUser(Long id, UserRole currentUserRole) {
        UserEntity user = getById(id);

        checkRoleHierarchy(currentUserRole, user.getRole(), "delete");

        refreshSessionService.revokeAllUserSessions(user.getId());
        repository.delete(user);
    }

    @Transactional
    public void revokeSessions(Long targetUserId, UserRole currentUserRole) {
        UserEntity user = getById(targetUserId);

        checkRoleHierarchy(currentUserRole, user.getRole(), "revoke sessions of");
        refreshSessionService.revokeAllUserSessions(user.getId());
    }

    public void validatePasswordStrength(String rawPassword) {
        if (rawPassword == null || !rawPassword.matches(Constants.PASSWORD_PATTERN)) {
            throw new IllegalArgumentException("Password does not meet security requirements. " +
                    "It must contain 8-60 characters, uppercase and lowercase letters, a digit, and a special character.");
        }
    }

    private void checkRoleHierarchy(UserRole currentUserRole, UserRole targetRole, String action) {
        if (targetRole == UserRole.SUPERADMIN) {
            throw new IllegalArgumentException(String.format("Nobody has permission to %s a user with role %s", action, targetRole));
        }

        if (currentUserRole == UserRole.ADMIN && targetRole == UserRole.ADMIN) {
            throw new IllegalArgumentException(String.format("You do not have permission to %s a user with role %s", action, targetRole));
        }
    }
}
