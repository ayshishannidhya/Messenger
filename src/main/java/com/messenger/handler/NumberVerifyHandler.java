package com.messenger.handler;

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

import com.messenger.pojo.NumberVerifyList;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
public class NumberVerifyHandler {

    private static final List<NumberVerifyList> list = new ArrayList<>();

    public boolean addNumber(@NonNull NumberVerifyList numberVerifyList) {
        log.info("Adding Number {}", numberVerifyList.getPhoneNumber());
        AtomicBoolean flag = new AtomicBoolean(false);
        list.stream()
                .filter(i -> Objects.equals(i.getPhoneNumber(), numberVerifyList.getPhoneNumber()))
                .findFirst()
                .ifPresentOrElse(i -> {
                    log.info("Number is already present, decreasing the otp limit of {}",
                            numberVerifyList.getPhoneNumber());
                    if (i.getOtpLimit() > 0) {
                        i.setOtpLimit(i.getOtpLimit() - 1);
                        flag.set(true);
                    } else {
                        log.info("Otp limit reached for {}", numberVerifyList.getPhoneNumber());
                        flag.set(false);
                    }

                }, () -> {
                    log.info("Number not found, Number added to list {}", numberVerifyList.getPhoneNumber());
                    list.add(numberVerifyList);
                    flag.set(true);
                });

        return flag.get();
    }

    public boolean isVerified(String number) {
        log.info("Checking number is verified or not - {}", number);
        return list.stream()
                .map(i -> Objects.equals(i.getPhoneNumber(), number) ? i.getVerified() : null)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(false);
    }

    public boolean markAsVerified(String number) {
        AtomicBoolean flag = new AtomicBoolean(false);
        list.stream()
                .filter(i -> Objects.equals(i.getPhoneNumber(), number))
                .findFirst()
                .ifPresentOrElse(i -> {
                    i.setVerified(true);
                    flag.set(true);
                }, () -> {
                    log.info("Number cant be verified :- {}", number);
                    flag.set(false);
                });

        return flag.get();
    }
}
