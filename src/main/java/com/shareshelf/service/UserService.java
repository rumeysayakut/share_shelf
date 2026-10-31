
package com.shareshelf.service;

import com.shareshelf.dto.request.UserRequestDto;
import com.shareshelf.dto.response.UserResponseDto;
import com.shareshelf.entity.User;
import com.shareshelf.entity.UserRole;
import com.shareshelf.mapper.UserMapper;
import com.shareshelf.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import com.shareshelf.dto.request.UserUpdateRequestDto;
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponseDto register(UserRequestDto userRequestDto) {

        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new IllegalArgumentException(
                    "Bu e-posta adresi zaten kayıtlı."
            );
        }

        User user = userMapper.toEntity(userRequestDto);

        user.setRole(UserRole.USER);

        user.setPassword(
                passwordEncoder.encode(userRequestDto.getPassword())
        );

        User savedUser = userRepository.save(user);

        return userMapper.toResponseDto(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {

        User user = findActiveUserById(id);

        return userMapper.toResponseDto(user);
    }


@Transactional(readOnly = true)
public List<UserResponseDto> getAllUsers() {
    return userRepository.findAllByDeletedAtIsNull()
            .stream()
            .map(userMapper::toResponseDto)
            .toList();
}


    @Transactional
    public UserResponseDto updateUser(Long id, UserUpdateRequestDto dto) {
        User user = findActiveUserById(id);

        userRepository.findByEmail(dto.getEmail())
                .filter(existingUser -> !existingUser.getId().equals(id))
                .ifPresent(existingUser -> {
                    throw new IllegalArgumentException("Bu e-posta adresi başka bir kullanıcıya ait.");
                });

        userMapper.updateEntity(dto, user);

        User updatedUser = userRepository.save(user);

        return userMapper.toResponseDto(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {

        User user = findActiveUserById(id);

        user.setDeletedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    private User findActiveUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Kullanıcı bulunamadı."
                ));

        if (user.getDeletedAt() != null) {
            throw new IllegalArgumentException(
                    "Bu kullanıcı hesabı silinmiş."
            );
        }

        return user;
    }
}
