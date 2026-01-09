package com.messenger.mappers;

import com.messenger.dto.UserCreateDTO;
import com.messenger.models.Users;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

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
@Component
public class UserMapper {
    public Users toEntity(@NonNull UserCreateDTO userCreateDTO) {

        return Users.builder()
                .firstName(userCreateDTO.getFirstName())
                .lastName(userCreateDTO.getLastName())
                .email(userCreateDTO.getEmail())
                .mobNumber(userCreateDTO.getMobNumber())
                .password(userCreateDTO.getPassword())
                .build();

    }
}
