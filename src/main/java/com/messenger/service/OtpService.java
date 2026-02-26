package com.messenger.service;

import com.messenger.dto.OtpVerifyDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

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
public interface OtpService {

    void sendMailOtp(@NotBlank(message = "Email cant be empty") @Email String email);

    String verifyOtp(@NotBlank(message = "Email cant be empty") OtpVerifyDto otpVerifyDto);

    void sendSmsOtp(@NotBlank(message = "Mobile Number cant be empty") String mobNumber);
}

