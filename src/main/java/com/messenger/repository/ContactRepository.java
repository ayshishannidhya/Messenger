package com.messenger.repository;

import com.messenger.models.Contacts;
import com.messenger.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    Optional<Contacts> findByPerson1AndPerson2(Users person1, Users person2);


    // Bidirectional lookup — handles (A,B) and (B,A) both
    @Query("""
                SELECT c FROM Contacts c
                WHERE (c.person1 = :userA AND c.person2 = :userB)
                   OR (c.person1 = :userB AND c.person2 = :userA)
            """)
    Optional<Contacts> findContact(
            @Param("userA") Users userA,
            @Param("userB") Users userB
    );

    // Same as above but WITH chats — use this when sending/reading messages
    @Query("""
                SELECT c FROM Contacts c
                LEFT JOIN FETCH c.chats
                WHERE (c.person1 = :userA AND c.person2 = :userB)
                   OR (c.person1 = :userB AND c.person2 = :userA)
            """)
    Optional<Contacts> findContactWithChats(
            @Param("userA") Users userA,
            @Param("userB") Users userB
    );
}
