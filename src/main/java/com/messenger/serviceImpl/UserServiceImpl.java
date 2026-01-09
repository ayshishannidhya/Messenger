package com.messenger.serviceImpl;

import com.messenger.dto.UserCreateDTO;
import com.messenger.mappers.UserMapper;
import com.messenger.models.Users;
import com.messenger.repository.UserRepository;
import com.messenger.service.UserService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/*
 * Copyright (c) 2026 Ayshi Shannidhya Panda. All rights reserved.
 *
 * This source code is confidential and intended solely for internal use.
 * Unauthorized copying, modification, distribution, or disclosure of this
 * file, via any medium, is strictly prohibited.
 *
 * Project: Messenger
 * Author: Ayshi Shannidhya Panda
 * Created on: 06-01-2026
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Users createUser(@NonNull UserCreateDTO userCreateDTO) {
        if (userRepository.existsByEmail(userCreateDTO.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        if (userRepository.existsByMobNumber(userCreateDTO.getMobNumber())) {
            throw new IllegalArgumentException("Mobile number already registered");
        }

         
        Users user = userMapper.toEntity(userCreateDTO);

        user.setPassword(passwordEncoder.encode(userCreateDTO.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public Users login(String email, String rawPassword) {

        Users users = userRepository.findByEmail(email);
        if (!passwordEncoder.matches(rawPassword, users.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        return users;
    }

}
