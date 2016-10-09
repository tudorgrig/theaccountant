package com.myMoneyTracker.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Tudor
 * This class is used to encrypt the app_user password
 */
public class PasswordEncrypt {

    private String algorithm;

    public String encryptPassword(String password) {
        if (password == null || password.length() == 0) {
            throw new IllegalArgumentException("Invalid password");
        }
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            Logger.getLogger(PasswordEncrypt.class.getName()).log(Level.SEVERE, noSuchAlgorithmException.getMessage());
        }
        md.update(password.getBytes());
        byte[] hash = md.digest();
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            if ((0xff & hash[i]) < 0x10) {
                hexString.append("0" + Integer.toHexString((0xFF & hash[i])));
            } else {
                hexString.append(Integer.toHexString(0xFF & hash[i]));
            }
        }
        return hexString.toString();
    }

    public String getAlgorithm() {

        return algorithm;
    }

    public void setAlgorithm(String algorithm) {

        this.algorithm = algorithm;
    }
}
