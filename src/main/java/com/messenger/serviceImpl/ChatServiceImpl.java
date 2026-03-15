package com.messenger.serviceImpl;

import com.messenger.config.WebSocketEventListener;
import com.messenger.dto.ChatMessageDto;
import com.messenger.dto.ContactsDto;
import com.messenger.mappers.ChatDtoMapper;
import com.messenger.models.Chat;
import com.messenger.models.Contacts;
import com.messenger.models.Users;
import com.messenger.repository.ChatRepository;
import com.messenger.repository.ContactRepository;
import com.messenger.repository.UserRepository;
import com.messenger.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;

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
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final SimpMessageSendingOperations messageTemplate;
    private final WebSocketEventListener webSocketEventListener;
    private final ContactRepository contactRepository;


    @Override
    public void sendPrivateMessage(String senderPhoneNumber,
                                   String receiverPhoneNumber,
                                   @NonNull ChatMessageDto chatMessageDto,
                                   Principal principal) {

        Users sender = userRepository.existsByMobNumber(senderPhoneNumber)
                .orElseThrow(() -> new UsernameNotFoundException("Sender: " + senderPhoneNumber));

        Users receiver = userRepository.existsByMobNumber(receiverPhoneNumber)
                .orElseThrow(() -> new UsernameNotFoundException("Receiver: " + receiverPhoneNumber));

        if (chatMessageDto.getMessage() == null || chatMessageDto.getMessage().isBlank()) {
            if (chatMessageDto.getFileUrl() == null || chatMessageDto.getFileUrl().isBlank()) {
                log.warn("The message or file is blank");
                return;
            }
        }

        Contacts contactsPresentOrNot = contactRepository.findByPerson1AndPerson2(sender, receiver)
                .orElse(null);

        if (contactsPresentOrNot != null) {
            Chat chat = buildChat(contactsPresentOrNot, chatMessageDto, sender, receiver);
            chat = chatRepository.save(chat);

            if (contactsPresentOrNot.getChats() == null) {
                contactsPresentOrNot.setChats(new ArrayList<>());
            }
            contactsPresentOrNot.getChats().add(chat);
            contactRepository.save(contactsPresentOrNot);

            // Send the saved chat message to receiver (if online)
            ChatMessageDto chatDto = ChatDtoMapper.toDto(chat);
            if (webSocketEventListener.isUserOnline(receiver.getMobNumber())) {
                messageTemplate.convertAndSendToUser(
                        receiver.getMobNumber(),
                        "/queue/messages",
                        chatDto
                );
            } else {
                // TODO: send push notification if user is not online
            }
            // Send acknowledgment back to sender
            messageTemplate.convertAndSendToUser(
                    principal.getName(),
                    "/queue/messages",
                    chatDto
            );
            return;
        }
        Contacts contacts = Contacts.builder()
                .person1(sender)
                .person2(receiver)
                .build();

        contacts = contactRepository.save(contacts);
        Chat chat = buildChat(contacts, chatMessageDto, sender, receiver);
        chat = chatRepository.save(chat);

        if (contacts.getChats() == null) {
            contacts.setChats(new ArrayList<>());
        }
        contacts.getChats().add(chat);
        contacts = contactRepository.save(contacts);
        ContactsDto contactsDto = ContactsDto.builder()
                .contactId(contacts.getContactId())
                .person1(contacts.getPerson1())
                .person2(contacts.getPerson2())
                .chats(contacts.getChats().stream()
                        .map(ChatDtoMapper::toDto).toList())
                .build();

        if (webSocketEventListener.isUserOnline(receiver.getMobNumber())) {
            messageTemplate.convertAndSendToUser(
                    receiver.getMobNumber(),
                    "/queue/messages",
                    contactsDto
            );
        } else {
            // TODO: send push notification if user is not online
        }

        // Send acknowledgment back to sender
        messageTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/messages",
                contactsDto
        );

    }

    private Chat buildChat(Contacts contacts,
                           @NonNull ChatMessageDto chatMessageDto,
                           Users sender,
                           Users receiver) {

        Chat.ChatBuilder chatBuilder = Chat.builder()
                .contacts(contacts)
                .messageType(chatMessageDto.getMessageType())
                .message(chatMessageDto.getMessage())
                .sender(sender)
                .receiver(receiver);

        if (chatMessageDto.getFileUrl() != null && chatMessageDto.getFileUrl().isBlank()) {
            chatBuilder.fileUrl(chatMessageDto.getFileUrl());
            chatBuilder.mediaType(chatMessageDto.getMediaType());
        }
        return chatBuilder.build();
    }
}
