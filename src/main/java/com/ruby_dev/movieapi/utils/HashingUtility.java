package com.ruby_dev.movieapi.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashingUtility {

    public static String createHash(String input){
       try{
           MessageDigest messageDigest = MessageDigest.getInstance("MD5");
           byte[] md = messageDigest.digest(input.getBytes());

           StringBuilder hexString = new StringBuilder();
           for (byte b : md) {
               hexString.append(String.format("%02x", b));
           }

           return hexString.toString();
       } catch (NoSuchAlgorithmException e) {
           throw new RuntimeException("Algorithm not found");
       }
    }
}
