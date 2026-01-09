package com.messenger.dto;

import jakarta.persistence.*;
import lombok.*;

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
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OtpVerifyDto {

    @Column(nullable = false)
    private String identifier;

    @Column(nullable = false)
    private String otp;

}
