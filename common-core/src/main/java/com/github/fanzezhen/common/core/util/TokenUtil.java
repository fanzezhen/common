package com.github.fanzezhen.common.core.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * @author zezhen.fan
 */
@Slf4j
public class TokenUtil {

    /**
     * Generate token for user login.
     *
     * @param username       用户名
     * @param tokenExpiredMs 有效时间
     * @return return a token string.
     */
    public static String createJwtToken(String username, String tokenSecret, long tokenExpiredMs) {
        Date expireTime = new Date(System.currentTimeMillis() + tokenExpiredMs);
        return createJwtToken(username, tokenSecret, expireTime);
    }

    /**
     * Generate token for user login.
     *
     * @param subject    用户名
     * @param expireTime 过期时间
     * @return return a token string.
     */
    public static String createJwtToken(String subject, String tokenSecret, Date expireTime) {
        return Jwts.builder()
                .setSubject(subject)
                .setExpiration(expireTime)
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS512, tokenSecret)
                .compact();
    }

    private Claims getClaimsFromToken(String token, String signKey) {
        return Jwts.parser()
                .setSigningKey(signKey)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Get subject(username) from Jwt, the subject(username) have set to jwt when generate token.
     *
     * @param token   jwt token.
     * @param signKey jwt sign key, set in properties file.
     * @return Subject(username or user id).
     */
    private String getSubjectFromToken(String token, String signKey) {
        try {
            return getClaimsFromToken(token, signKey).getSubject();
        } catch (ExpiredJwtException e) {
            // 过期则从ExpiredJwtException中取username
            return e.getClaims().getSubject();
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * validate token eligible.
     * if Jwts can parse the token string and no throw any exception, then the token is eligible.
     *
     * @param token a jws string.
     */
    public static boolean validateToken(String token, String tokenSecret) {
        String validateFailed = "validate failed : ";
        // ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException
        try {
            Jwts.parser().setSigningKey(tokenSecret).parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            log.error(validateFailed + ex.getMessage());
            return false;
        }
    }
}
