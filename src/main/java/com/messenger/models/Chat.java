package com.messenger.models;

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

import com.messenger.enumerated.MediaType;
import com.messenger.enumerated.MessageType;
import com.messenger.enumerated.Reactions;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private Contacts contacts;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private Users sender;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private Users receiver;

    @Enumerated(value = EnumType.STRING)
    private MessageType messageType;

    @Enumerated(EnumType.STRING)
    private MediaType mediaType;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(columnDefinition = "TEXT")
    private String fileUrl;

    @Enumerated(value = EnumType.STRING)
    private Reactions senderReaction;

    @Enumerated(EnumType.STRING)
    private Reactions receiverReaction;

    @Builder.Default
    private Boolean isEdited = false;

    @Column(columnDefinition = "TEXT")
    private String editedMessage;

    @Builder.Default
    private Boolean isReceived = false;

    @Builder.Default
    private Boolean isRead = false;

    private LocalDateTime readAt;

    @Builder.Default
    private Boolean isDeleted = false;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
