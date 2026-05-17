package com.github.randdd32.donor_search_backend.service.auth;

import com.github.randdd32.donor_search_backend.core.error.NotFoundException;
import com.github.randdd32.donor_search_backend.model.auth.UserEntity;
import com.github.randdd32.donor_search_backend.model.enums.UserRole;
import com.github.randdd32.donor_search_backend.repository.auth.UserRepository;
import com.github.randdd32.donor_search_backend.web.dto.auth.UserCreateDto;
import com.github.randdd32.donor_search_backend.web.dto.auth.UserUpdateDto;
import com.github.randdd32.donor_search_backend.web.dto.filter.UserFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository repository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RefreshSessionService refreshSessionService;

    @InjectMocks
    private UserService userService;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPasswordHash("hashedPass");
        user.setRole(UserRole.USER);
    }

    @Test
    @DisplayName("Позитивный тест: получение списка пользователей (getAll)")
    void getAll_Positive() {
        Pageable pageable = PageRequest.of(0, 10);
        UserFilter filter = new UserFilter("test", null, null, null, null, null);
        Page<UserEntity> page = new PageImpl<>(List.of(user));

        when(repository.findAll(Mockito.<Specification<UserEntity>>any(), eq(pageable))).thenReturn(page);

        Page<UserEntity> result = userService.getAll(filter, pageable);
        assertEquals(1, result.getTotalElements());
        verify(repository, times(1)).findAll(Mockito.<Specification<UserEntity>>any(), eq(pageable));
    }

    @Test
    @DisplayName("Позитивный тест: получение пользователя по ID")
    void getById_Positive() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        UserEntity found = userService.getById(1L);
        assertEquals("testuser", found.getUsername());
    }

    @Test
    @DisplayName("Негативный тест: пользователь по ID не найден")
    void getById_Negative_NotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.getById(99L));
    }

    @Test
    @DisplayName("Позитивный тест: получение пользователя по username")
    void getByUsername_Positive() {
        when(repository.findByUsernameIgnoreCase("testuser")).thenReturn(Optional.of(user));
        UserEntity found = userService.getByUsername("testuser");
        assertEquals(1L, found.getId());
    }

    @Test
    @DisplayName("Позитивный тест: создание пользователя (сложный пароль проходит валидацию)")
    void createUser_Positive() {
        UserCreateDto dto = new UserCreateDto("newuser", "SecurePass123!", UserRole.ADMIN);

        when(repository.findByUsernameIgnoreCase("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("SecurePass123!")).thenReturn("hash");
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserEntity created = userService.createUser(dto, UserRole.SUPERADMIN);

        assertEquals("newuser", created.getUsername());
        assertEquals(UserRole.ADMIN, created.getRole());
        assertEquals("hash", created.getPasswordHash());
    }

    @Test
    @DisplayName("Негативный тест: создание пользователя со слабым паролем")
    void createUser_Negative_WeakPassword() {
        UserCreateDto dto = new UserCreateDto("newuser", "weak", UserRole.USER);
        when(repository.findByUsernameIgnoreCase("newuser")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                userService.createUser(dto, UserRole.SUPERADMIN)
        );
        assertTrue(ex.getMessage().contains("Password does not meet security requirements"));
    }

    @Test
    @DisplayName("Негативный тест: иерархия ролей (ADMIN пытается создать ADMIN'а)")
    void createUser_Negative_RoleHierarchyViolation() {
        UserCreateDto dto = new UserCreateDto("newuser", "SecurePass123!", UserRole.ADMIN);
        when(repository.findByUsernameIgnoreCase("newuser")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                userService.createUser(dto, UserRole.ADMIN)
        );
        assertTrue(ex.getMessage().contains("You do not have permission"));
    }

    @Test
    @DisplayName("Позитивный тест: успешное обновление своего профиля (смена пароля)")
    void updateUser_Positive_SelfUpdate() {
        UserUpdateDto dto = new UserUpdateDto(UserRole.USER, "NewSecurePass123!");

        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("NewSecurePass123!")).thenReturn("newHash");
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserEntity updated = userService.updateUser(1L, dto, UserRole.USER, "testuser");

        assertEquals("newHash", updated.getPasswordHash());
        verify(refreshSessionService, times(1)).revokeAllUserSessions(1L);
    }

    @Test
    @DisplayName("Позитивный тест: обновление чужого профиля администратором (изменение роли)")
    void updateUser_Positive_ByAdmin() {
        UserUpdateDto dto = new UserUpdateDto(UserRole.ADMIN, null);

        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserEntity updated = userService.updateUser(1L, dto, UserRole.SUPERADMIN, "superadmin_user");

        assertEquals(UserRole.ADMIN, updated.getRole());
        verify(refreshSessionService, never()).revokeAllUserSessions(any());
    }

    @Test
    @DisplayName("Негативный тест: попытка изменения своей роли")
    void updateUser_Negative_SelfRoleChange() {
        UserUpdateDto dto = new UserUpdateDto(UserRole.ADMIN, null);
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                userService.updateUser(1L, dto, UserRole.USER, "testuser")
        );
        assertEquals("You cannot change your own role", ex.getMessage());
    }

    @Test
    @DisplayName("Позитивный тест: успешный отзыв сессий другого пользователя")
    void revokeSessions_Positive() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        userService.revokeSessions(1L, UserRole.ADMIN);

        verify(refreshSessionService, times(1)).revokeAllUserSessions(1L);
    }

    @Test
    @DisplayName("Негативный тест: попытка отзыва сессий пользователя выше по рангу (SUPERADMIN)")
    void revokeSessions_Negative_RoleHierarchy() {
        user.setRole(UserRole.SUPERADMIN);
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                userService.revokeSessions(1L, UserRole.ADMIN)
        );
        assertTrue(ex.getMessage().contains("Nobody has permission to revoke sessions of a user with role SUPERADMIN"));
    }
}
