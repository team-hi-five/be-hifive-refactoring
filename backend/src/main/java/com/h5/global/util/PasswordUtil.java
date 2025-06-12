package com.h5.global.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class PasswordUtil {

    @Value("${password.characters}")
    private String characters;

    @Value("${password.length}")
    private int length;

    private final SecureRandom random = new SecureRandom();

    public String generatePassword() {
        StringBuilder pwd = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int idx = random.nextInt(characters.length());
            pwd.append(characters.charAt(idx));
        }
        return pwd.toString();
    }
}
