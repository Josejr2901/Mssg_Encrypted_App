package com.example.encryptedmessagingapp.utils;

import java.security.SecureRandom;

public class SecureRandomUtils {
    private static final SecureRandom secureRandom = new SecureRandom();

    public static SecureRandom getRandom() {
        return secureRandom;
    }
}
