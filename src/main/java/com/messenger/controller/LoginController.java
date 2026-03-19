package com.messenger.controller;

/*
 * Copyright (c) 2026 Ayshi Shannidhya Panda. All rights reserved.
 *
 * This source code is confidential and intended solely for internal use.
 * Unauthorized copying, modification, distribution, or disclosure of this
 * file, via any medium, is strictly prohibited.
 *
 * Project: Messenger
 * Author: Ayshi Shannidhya Panda
 * Created on: 16-03-2026
 */

import com.messenger.dto.ApiResponse;
import com.messenger.dto.UserResponseDto;
import com.messenger.models.Users;
import com.messenger.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestParam String mobNumber,
            @RequestParam String password,
            @RequestParam String deviceModel,
            @RequestParam String deviceOs,
            @RequestParam String deviceId,
            @RequestParam String deviceToken,
            HttpServletRequest request
    ) {
        log.info("Login request received for mobNumber: {} {} from IP: {}", mobNumber, password, getClientIp(request));
        log.debug("Login attempt details - deviceModel: {}, deviceOs: {}, deviceId: {}", deviceModel, deviceOs,
                deviceId);

        if (mobNumber == null || mobNumber.isBlank() ||
                password == null || password.isBlank() ||
                deviceModel == null || deviceModel.isBlank() ||
                deviceOs == null || deviceOs.isBlank() ||
                deviceId == null || deviceId.isBlank() ||
                deviceToken == null || deviceToken.isBlank()) {

            log.warn("Login failed for mobNumber: {} - one or more required fields are empty", mobNumber);
            return new ResponseEntity<>(
                    ApiResponse.error(HttpStatus.BAD_REQUEST, "Bad Request", "All fields are required",
                            request.getRequestURI()),
                    HttpStatus.BAD_REQUEST);
        }

        Users user = userRepository.findByMobNumber(mobNumber)
                .orElseThrow(() -> new UsernameNotFoundException("User does not exists"));
        log.debug("User found: {} - checking account status", mobNumber);

        if (user == null) {
            log.warn("Login failed - mobNumber does not exist: {}", mobNumber);
            throw new UsernameNotFoundException("Username does not exists");
        }

//        if (user.isBlocked()) {
//            log.warn("Login failed for mobNumber: {} - account is blocked", mobNumber);
//            throw new DisabledException("Your account has been blocked");
//        }
//
//        if (user.isDeleted()) {
//            log.warn("Login failed for mobNumber: {} - account is deleted", mobNumber);
//            return new ResponseEntity<>(
//                    ApiResponse.error(HttpStatus.UNAUTHORIZED, "Unauthorized", "Your account has been deleted",
//                            request.getRequestURI()),
//                    HttpStatus.UNAUTHORIZED);
//        }
//
//        if (!user.isNotExpired()) {
//            log.warn("Login failed for mobNumber: {} - account is expired", mobNumber);
//            return new ResponseEntity<>(
//                    ApiResponse.error(HttpStatus.UNAUTHORIZED, "Unauthorized", "Your account has expired",
//                            request.getRequestURI()),
//                    HttpStatus.UNAUTHORIZED);
//        }
//
//        if (!user.isNotLocked()) {
//            log.warn("Login failed for mobNumber: {} - account is locked", mobNumber);
//            throw new LockedException("Your account is locked");
//        }
//
//        if (!user.isCredentialsNotExpired()) {
//            log.warn("Login failed for mobNumber: {} - credentials expired", mobNumber);
//            return new ResponseEntity<>(
//                    ApiResponse.error(HttpStatus.UNAUTHORIZED, "Unauthorized", "Your credentials have expired",
//                            request.getRequestURI()),
//                    HttpStatus.UNAUTHORIZED);
//        }

        if (!user.getIsPhoneNumberVerified()) {
            log.warn("Login failed for mobNumber: {} - phone number not verified", mobNumber);
            return new ResponseEntity<>(
                    ApiResponse.error(HttpStatus.UNAUTHORIZED, "Unauthorized", "Your phone number is not verified.",
                            request.getRequestURI()),
                    HttpStatus.UNAUTHORIZED);
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("Login failed for mobNumber: {} - invalid password", mobNumber);
            throw new BadCredentialsException("Invalid mobNumber or password");
        }

        log.debug("Password verified for mobNumber: {} - proceeding with authentication", mobNumber);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authToken);
        SecurityContextHolder.setContext(securityContext);

        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
        session.setAttribute("username", user.getUsername());
        session.setAttribute("phone", user.getMobNumber());
        session.setAttribute("deviceId", deviceId);
        session.setAttribute("deviceModel", deviceModel);
        session.setAttribute("deviceOs", deviceOs);
        session.setAttribute("deviceToken", deviceToken);
        session.setAttribute("ipAddress", getClientIp(request));
        session.setAttribute("userAgent", request.getHeader("User-Agent"));
        session.setAttribute("loginTime", Instant.now().toString());

        log.info("Login successful for mobNumber: {} - sessionId: {}", mobNumber, session.getId());
        log.debug("Session created with attributes - deviceId: {}, deviceModel: {}, deviceOs: {}, ipAddress: {}",
                deviceId, deviceModel, deviceOs, getClientIp(request));

        UserResponseDto responseData = UserResponseDto.builder()
                .mobNumber(user.getMobNumber())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
//                .profilePicUrl(user.getProfilePicUrl())
//                .bio(user.getBio())
//                .status(user.getStatus())
//                .lastSeen(user.getLastSeen())
                .build();

        return new ResponseEntity<>(
                ApiResponse.success(
                        responseData, "Login successful",
                        request.getRequestURI()),
                HttpStatus.OK);
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }
}
