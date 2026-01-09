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
 * Created on: 06-01-2026
 */

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreateDTO {

    @NotBlank(message = "First Name cant be empty")
    @Size(min = 2, max = 15)
    private String firstName;

    @NotBlank(message = "Last Name cant be empty")
    @Size(min = 2, max = 15)
    private String lastName;

    @NotBlank(message = "Mobile Number cant be empty")
    private String mobNumber;

    @NotBlank(message = "Email cant be empty")
    @Email
    private String email;

    @NotBlank(message = "Password cant be empty")
    private String password;
}
