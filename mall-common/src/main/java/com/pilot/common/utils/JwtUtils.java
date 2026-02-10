package com.pilot.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtUtils {
    // 密钥至少需要 32 位字符
    private static final String SECRET = "PilotMall-Seckill-AI-System-SecretKey-2026-v1.0-cc-Auth";
    private static final long EXPIRATION = 3600 * 24; // 24小时
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    public static String createToken(Long userId, String username) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId)) // 0.11.5 使用 setSubject
                .claim("username", username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION * 1000)) // 0.11.5 使用 setExpiration
                .signWith(KEY, SignatureAlgorithm.HS256) // 0.11.5 显式指定算法
                .compact();
    }

    public static Claims parseToken(String token) {
        return Jwts.parserBuilder() // 0.11.5 使用 parserBuilder()
                .setSigningKey(KEY)   // 0.11.5 使用 setSigningKey
                .build()
                .parseClaimsJws(token) // 0.11.5 使用 parseClaimsJws
                .getBody();            // 0.11.5 使用 getBody
    }

    public static Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return Long.parseLong(claims.getSubject());
    }

    public static String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("username", String.class);
    }

    public static boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}