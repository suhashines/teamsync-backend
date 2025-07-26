package edu.teamsync.teamsync.config;

import java.util.Base64;

public class KeyGenerator {
    public static void main(String[] args) throws Exception {
        javax.crypto.KeyGenerator keyGen = javax.crypto.KeyGenerator.getInstance("HmacSHA256");
        byte[] key = keyGen.generateKey().getEncoded();
        System.out.println(Base64.getEncoder().encodeToString(key));
    }
}