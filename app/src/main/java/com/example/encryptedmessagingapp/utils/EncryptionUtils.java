package com.example.encryptedmessagingapp.utils;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtils {
    private static final String AES = "AES";
    private static final String AES_MODE = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    // Generate a random AES key
    public static SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(AES);
        keyGen.init(256);
        return keyGen.generateKey();
    }

    public static String encrypt(String plainText, SecretKey key) throws Exception {
        if (key == null) throw new IllegalStateException("AES Key is null");

        Cipher cipher = Cipher.getInstance(AES_MODE);
        byte[] iv = new byte[GCM_IV_LENGTH];
        SecureRandomUtils.getRandom().nextBytes(iv);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

        cipher.init(Cipher.ENCRYPT_MODE, key, spec);
        byte[] encrypted = cipher.doFinal(plainText.getBytes());

        byte[] combined = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

        return Base64.encodeToString(combined, Base64.DEFAULT);
    }


    // Decrypt a message with AES key
    public static String decrypt(String cipherText, SecretKey key) throws Exception {
        byte[] combined = Base64.decode(cipherText, Base64.DEFAULT);
        byte[] iv = new byte[GCM_IV_LENGTH];
        byte[] encrypted = new byte[combined.length - GCM_IV_LENGTH];
        System.arraycopy(combined, 0, iv, 0, GCM_IV_LENGTH);
        System.arraycopy(combined, GCM_IV_LENGTH, encrypted, 0, encrypted.length);
        Cipher cipher = Cipher.getInstance(AES_MODE);
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH, iv));
        byte[] decrypted = cipher.doFinal(encrypted);
        return new String(decrypted);
    }

    public static SecretKey decodeKey(String base64Key) {
        byte[] decodedKey = Base64.decode(base64Key, Base64.DEFAULT);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, AES);
    }

    public static String encodeKey(SecretKey key) {
        return Base64.encodeToString(key.getEncoded(), Base64.DEFAULT);
    }
}
