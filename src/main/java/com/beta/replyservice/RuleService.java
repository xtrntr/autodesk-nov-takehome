package com.beta.replyservice;

import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.math.BigInteger;

@Service
public class RuleService {

    public String applyRule(String rulestring) {
        String[] res = rulestring.split("-", 2);
        if (res.length < 2 || res[0].isEmpty()) {
            throw new IllegalArgumentException("Invalid format");
        }
        String rule = res[0];
        String result = res[1];
        for (char operation : rule.toCharArray()) {
            switch (operation) {
                case '1':
                    result = new StringBuilder(result).reverse().toString();
                    break;
                case '2':
                    result = md5(result);
                    break;
                default:
                    throw new IllegalArgumentException(String.format("Invalid rule: %s", rule));
            }
        }
        return result;
    }

    private String md5(String string) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(string.getBytes(StandardCharsets.UTF_8));
            return String.format("%032x", new BigInteger(1, digest));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}