package com.messenger.serviceImpl;

import com.messenger.dto.OtpVerifyDto;
import com.messenger.handler.NumberVerifyHandler;
import com.messenger.models.Otp;
import com.messenger.otp.OtpGenerator;
import com.messenger.pojo.OtpPayload;
import com.messenger.repository.OtpRepository;
import com.messenger.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

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

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final JavaMailSender javaMailSender;
    private final SmsService smsService;
    private final NumberVerifyHandler numberVerifyHandler;


    @Override
    public void sendMailOtp(String email) {
        String otp = OtpGenerator.generateOtp(6);

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject("Your Messenger OTP");
        simpleMailMessage.setText(
                "Dear user,\n\nYour OTP is: " + otp + "\nIt will expire in 5 minutes.\n\nRegards,\nMessenger");

        javaMailSender.send(simpleMailMessage);
        otpRepository.save(Otp.builder()
                .otp(otp)
                .identifier(email)
                .isUsed(false)
                .isOtpExpired(false)
                .expiryTime(LocalDateTime.now().plusSeconds(300))
                .build());
    }

    @Override
    public String verifyOtp(@NonNull OtpVerifyDto otpVerifyDto) {

        Optional<Otp> otpOptional = otpRepository.findByIdentifier(otpVerifyDto.getIdentifier());

        Otp record = otpOptional.orElseThrow(() -> new IllegalArgumentException("No OTP found for identifier"));
        if (record.isUsed()) {
            return "Otp already used";
        }
        if (record.getExpiryTime() != null && record.getExpiryTime().isBefore(LocalDateTime.now())) {
            record.setOtpExpired(true);
            otpRepository.save(record);
            return "Otp expired";
        }
        if (!record.getOtp().equals(otpVerifyDto.getOtp())) {
            return "Wrong OTP entered";
        }

        record.setUsed(true);
        record.setOtpExpired(false);
        otpRepository.save(record);
        return "Otp Verified Successfully";
    }

    @Override
    public void sendSmsOtp(String mobNumber) {
        String otp = OtpGenerator.generateOtp(6);
        

        smsService.sendSms(new OtpPayload(mobNumber,
                "Your Messenger OTP is: " + otp + " (valid 5 minutes)"));
        otpRepository.save(Otp.builder()
                .otp(otp)
                .identifier(mobNumber)
                .isUsed(false)
                .isOtpExpired(false)
                .expiryTime(LocalDateTime.now().plusSeconds(300))
                .build());
    }
}
