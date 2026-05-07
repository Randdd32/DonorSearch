package com.github.randdd32.donor_search_backend.web.controller.auth;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import com.github.randdd32.donor_search_backend.core.log.NoLogging;
import com.github.randdd32.donor_search_backend.model.enums.UserRole;
import com.github.randdd32.donor_search_backend.service.auth.UserService;
import com.github.randdd32.donor_search_backend.web.dto.auth.UserCreateDto;
import com.github.randdd32.donor_search_backend.web.dto.auth.UserDto;
import com.github.randdd32.donor_search_backend.web.dto.auth.UserUpdateDto;
import com.github.randdd32.donor_search_backend.web.dto.filter.UserFilter;
import com.github.randdd32.donor_search_backend.web.dto.pagination.PageDto;
import com.github.randdd32.donor_search_backend.web.mapper.auth.UserMapper;
import com.github.randdd32.donor_search_backend.web.mapper.pagination.PageDtoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Constants.API_URL + "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/me")
    public UserDto getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return userMapper.toDto(userService.getByUsername(username));
    }

    @NoLogging
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @GetMapping
    public PageDto<UserDto> getAllUsers(
            @ModelAttribute UserFilter filter,
            @PageableDefault(size = Constants.DEFAULT_PAGE_SIZE, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return PageDtoMapper.toDto(userService.getAll(filter, pageable), userMapper::toDto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        return userMapper.toDto(userService.getById(id));
    }

    @NoLogging
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody UserCreateDto dto, Authentication authentication) {
        UserRole currentUserRole = extractRole(authentication);
        return userMapper.toDto(userService.createUser(dto, currentUserRole));
    }

    @NoLogging
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @PutMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDto dto, Authentication authentication) {
        UserRole currentUserRole = extractRole(authentication);
        String currentUsername = authentication.getName();
        return userMapper.toDto(userService.updateUser(id, dto, currentUserRole, currentUsername));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id, Authentication authentication) {
        String currentUsername = authentication.getName();
        UserRole currentUserRole = extractRole(authentication);

        if (userService.getByUsername(currentUsername).getId().equals(id)) {
            throw new IllegalArgumentException("You cannot delete your own account");
        }

        userService.deleteUser(id, currentUserRole);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @PostMapping("/{id}/revoke-sessions")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revokeUserSessions(@PathVariable Long id, Authentication authentication) {
        UserRole currentUserRole = extractRole(authentication);
        userService.revokeSessions(id, currentUserRole);
    }

    private UserRole extractRole(Authentication authentication) {
        String roleStr = authentication.getAuthorities().iterator().next().getAuthority();
        return UserRole.valueOf(roleStr.replace("ROLE_", ""));
    }
}
