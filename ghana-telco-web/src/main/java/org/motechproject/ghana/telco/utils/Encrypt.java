package org.motechproject.ghana.telco.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class Encrypt {

    public static String encrypt(String plaintext) {
        try {
            return DigestUtils.sha256Hex(plaintext);
        } catch (Exception e) {
            throw new RuntimeException("No Such Algorithm Exists", e);
        }
    }
}
