package com.messenger.otp;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;

import java.security.SecureRandom;

/*
 * Copyright (c) 2026 Ayshi Shannidhya Panda. All rights reserved.
 *
 * This source code is confidential and intended solely for internal use.
 * Unauthorized copying, modification, distribution, or disclosure of this
 * file, via any medium, is strictly prohibited.
 *
 * Project: Messenger
 * Author: Ayshi Shannidhya Panda
 * Created on: 07-01-2026
 */

@RequiredArgsConstructor
public class OtpGenerator {
    private static final SecureRandom secureRandom = new SecureRandom();

    public static @NonNull String generateOtp(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            stringBuilder.append(secureRandom.nextInt(10));
        }
        return stringBuilder.toString();
    }
}
