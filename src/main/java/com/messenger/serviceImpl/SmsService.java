package com.messenger.serviceImpl;

/*
 * Copyright (c) 2026 Ayshi Shannidhya Panda. All rights reserved.
 *
 * This source code is confidential and intended solely for internal use.
 * Unauthorized copying, modification, distribution, or disclosure of this
 * file, via any medium, is strictly prohibited.
 *
 * Project: Messenger
 * Author: Ayshi Shannidhya Panda
 * Created on: 08-01-2026
 */

import com.messenger.pojo.OtpPayload;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@ConfigurationProperties(prefix = "sms")
@Getter
@Setter
@Slf4j
public class SmsService {

    @Value("${textbee.api.key}")
    private String api;

    @Value("${textbee.device.id}")
    private String device;

    @Async
    public void sendSms(@NonNull OtpPayload otpPayload) {
        String url = "https://api.textbee.dev/api/v1/gateway/devices/" + device + "/send-sms";

        log.info("Sending SMS to phone number: {}", maskPhoneNumber(otpPayload.number()));
        log.debug("SMS API URL: {}, Device ID: {}", url, device);

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", api);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = Map.of(
                "recipients", List.of(otpPayload.number()),
                "message", otpPayload.message()
        );

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );
            log.info("SMS sent successfully to: {} - Status: {}", maskPhoneNumber(otpPayload.number()),
                    response.getStatusCode());
            log.debug("SMS API response: {}", response.getBody());
        } catch (Exception e) {
            log.error("Failed to send SMS to: {} - Error: {}", maskPhoneNumber(otpPayload.number()), e.getMessage());
            throw e;
        }
    }

    private @NonNull String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 4) {
            return "****";
        }
        return phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(phoneNumber.length() - 2);
    }
}
