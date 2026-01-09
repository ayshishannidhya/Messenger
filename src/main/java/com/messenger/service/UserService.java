package com.messenger.service;

import com.messenger.dto.UserCreateDTO;
import com.messenger.models.Users;

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
public interface UserService {
    Users createUser(UserCreateDTO userCreateDTO);

    Users login(String email, String rawPassword);
}
