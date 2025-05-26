package edu.teamsync.teamsync.config;

import javax.crypto.KeyGenerator;
import java.util.Base64;

public class KeyGeneratorUtil {
    public static void main(String[] args) throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
        byte[] key = keyGen.generateKey().getEncoded();
        System.out.println(Base64.getEncoder().encodeToString(key));
    }
}