package com.messenger.dto;

import com.messenger.models.Users;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/*
 * Copyright (c) 2026 Ayshi Shannidhya Panda. All rights reserved.
 *
 * This source code is confidential and intended solely for internal use.
 * Unauthorized copying, modification, distribution, or disclosure of this
 * file, via any medium, is strictly prohibited.
 *
 * Project: Messenger
 * Author: Ayshi Shannidhya Panda
 * Created on: 15-03-2026
 */
@Builder
@Data
public class ContactsDto {

    private Long contactId;

    private Users person1;

    private Users person2;

    private List<ChatMessageDto> chats;
}
