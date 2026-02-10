package pcy.study.server.utils;

import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class SHA256Util {

    public static final String ENCRYPTION_KEY = "SHA-256";

    public static String encryptSHA256(String input) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(ENCRYPTION_KEY);
            messageDigest.update(input.getBytes());
            byte[] digest = messageDigest.digest();

            StringBuilder builder = new StringBuilder();
            for (byte message : digest) {
                builder.append(Integer.toString((message & 0xff) + 0x100, 16).substring(1));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("암호화 에러!", e);
        }
    }
}
