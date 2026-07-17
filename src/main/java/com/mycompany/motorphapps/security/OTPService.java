/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorphapps.security;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class OTPService {

    private final ConcurrentHashMap<String, String> otpStorage = new ConcurrentHashMap<>();

    public String generateOTP(String username) {
        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        otpStorage.put(username, otp);

        // 🔥 Simulate sending (for demo)
        System.out.println("OTP for " + username + ": " + otp);

        return otp;
    }

    public boolean verifyOTP(String username, String inputOtp) {
        String correctOtp = otpStorage.get(username);

        if (correctOtp != null && correctOtp.equals(inputOtp)) {
            otpStorage.remove(username); // one-time use
            return true;
        }

        return false;
    }
}