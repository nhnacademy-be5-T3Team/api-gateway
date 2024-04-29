package com.t3t.apigateway.common;

import com.t3t.apigateway.exception.TokenNotAuthenticatedExceptions;
import com.t3t.apigateway.exception.TokenNotConsistedProperly;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;

import java.security.Key;
import java.util.Date;

/**
 * JWT 토큰 발행 및 검증을 위한 클래스
 * @author joohyun1996(이주현)
 */
@Component
public class JwtUtils {
    private Key key;

    public JwtUtils(@Value("${t3t.secret.key}") String secret) {
        byte[] byteSecretKey = Base64.getDecoder().decode(secret);
        key = Keys.hmacShaKeyFor(byteSecretKey);
    }

    public String getUserId(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get("username", String.class);
    }

    public String getRole(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get("role", String.class);
    }

    public String getCategory(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get("category", String.class);
    }

    public String getUUID(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get("uuid", String.class);
    }

    // 만료되었으면 true, 아니면 false
    public Boolean isExpired(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getExpiration().before(new Date());
    }

    public Boolean getValidation(String token) {
        try {
            // 토큰 검사후 false return(if에 return)
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getSignature();
            return false;
        } catch (ExpiredJwtException e) {
            return false;
        } catch (SignatureException e) {
            throw new TokenNotAuthenticatedExceptions("Token Authorization Failed");
        } catch (JwtException e) {
            throw new TokenNotConsistedProperly("Use Proper Tokens");
        } catch (Exception e) {
            throw new RuntimeException("e");
        }
    }

    /**
     * 토큰의 만료시간이 5분 전인지 확인을 위한 메소드
     * @param token
     * @return boolean
     */
    public Boolean checkReIssue(String token) {
        Date expiration = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getExpiration();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(expiration.toInstant(), ZoneId.systemDefault());
        Duration duration = Duration.between(LocalDateTime.now(), localDateTime);

        long diffSec = Math.abs(duration.toSeconds());

        return diffSec > 0 && diffSec <= 300;
    }

}

