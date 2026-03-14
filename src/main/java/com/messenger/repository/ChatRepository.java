package com.messenger.repository;

import com.messenger.models.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

/*
 * Copyright (c) 2026 Ayshi Shannidhya Panda. All rights reserved.
 *
 * This source code is confidential and intended solely for internal use.
 * Unauthorized copying, modification, distribution, or disclosure of this
 * file, via any medium, is strictly prohibited.
 *
 * Project: Messenger
 * Author: Ayshi Shannidhya Panda
 * Created on: 14-03-2026
 */
public interface ChatRepository extends JpaRepository<Chat, Long> {
}
