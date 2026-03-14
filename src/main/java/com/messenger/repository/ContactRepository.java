package com.messenger.repository;

import com.messenger.models.Contacts;
import com.messenger.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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
@Repository
public interface ContactRepository extends JpaRepository<Contacts, Long> {
    Optional<Contacts> existsBySenderAndReceiver(Users Sender, Users Receiver);
}
