package com.example.springboot.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.springboot.entity.User;

import java.util.Date;

public class TokenUtils {

    private static final long EXPIRE_TIME = 24 * 60 * 60 * 1000; // 24小时
    private static final String SECRET = "office-learning-platform";

    public static String genToken(Integer userId, String password) {
        Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        return JWT.create()
                .withClaim("userId", userId)
                .withClaim("password", password)
                .withExpiresAt(date)
                .sign(algorithm);
    }

    public static Integer getUserId(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }
        try {
            return JWT.decode(token).getClaim("userId").asInt();
        } catch (Exception e) {
            return null;
        }
    }

    public static String getPassword(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }
        try {
            return JWT.decode(token).getClaim("password").asString();
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean verifyToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWT.require(algorithm).build().verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}