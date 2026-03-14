package com.messenger.mappers;

import com.messenger.dto.ChatMessageDto;
import com.messenger.models.Chat;
import org.jspecify.annotations.NonNull;

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
public class ChatDtoMapper {

    public static ChatMessageDto toDto(@NonNull Chat chat) {
        return ChatMessageDto.builder()
                .id(chat.getChatId())
                .sender(chat.getSender().getMobNumber())
                .receiver(chat.getReceiver().getMobNumber())
                .messageType(chat.getMessageType())
                .mediaType(chat.getMediaType())
                .message(chat.getMessage())
                .fileUrl(chat.getFileUrl())
                .senderReaction(chat.getSenderReaction())
                .receiverReaction(chat.getReceiverReaction())
                .isEdited(chat.getIsEdited())
                .editedMessage(chat.getEditedMessage())
                .isReceived(chat.getIsReceived())
                .isRead(chat.getIsRead())
                .readAt(chat.getReadAt())
                .build();
    }
}
