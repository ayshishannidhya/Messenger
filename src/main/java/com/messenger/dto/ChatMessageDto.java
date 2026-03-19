package com.messenger.dto;

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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.messenger.enumerated.MediaType;
import com.messenger.enumerated.MessageType;
import com.messenger.enumerated.Reactions;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {

    private Long id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String sender;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String receiver;

    @Enumerated(value = EnumType.STRING)
    @NotNull(message = "MessageType can't be null")
    private MessageType messageType;

    @Enumerated(EnumType.STRING)
    private MediaType mediaType;


    private String message;

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
    @JsonIgnore
    private Boolean isDeleted = false;
}
