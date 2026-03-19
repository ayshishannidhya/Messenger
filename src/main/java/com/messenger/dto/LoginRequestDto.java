package com.messenger.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequestDto {

    @NotBlank(message = "Email cant be empty")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password cant be empty")
    private String password;
}
