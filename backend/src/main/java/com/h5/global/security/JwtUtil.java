package com.h5.global.security;

import com.github.hyeonjaez.springcommon.exception.BusinessException;
import com.h5.global.exception.DomainErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60; // 1 hour
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 14; // 14 days
    private static final long NEAR_EXPIRY_BUFFER_TIME = 1000 * 60 * 10; // 10 minutes

    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(userDetails, ACCESS_TOKEN_EXPIRE_TIME);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(userDetails, REFRESH_TOKEN_EXPIRE_TIME);
    }

    private String generateToken(UserDetails userDetails, long expireTime) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expireTime);

        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new IllegalArgumentException("User does not have any role"));

        Claims claims = Jwts.claims().setSubject(userDetails.getUsername());
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public String getEmailFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public String getRoleFromToken(String token) {
        return getClaimsFromToken(token).get("role", String.class);
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            if (claims.getExpiration().before(new Date())) {
                throw new ExpiredJwtException(null, claims, "JWT token is expired");
            }
            return true;
        } catch (ExpiredJwtException e) {
            throw new BusinessException(DomainErrorCode.EXPIRED_JWT);
        } catch (BusinessException e){
            throw new BusinessException(DomainErrorCode.INVALID_JWT);
        }
    }

    public long getRemainExpiredTime(String token) {
        try {
            Claims claims = getClaimsFromToken(token);

            long expirationTime = claims.getExpiration().getTime();
            long currentTime = System.currentTimeMillis();
            return Math.max(expirationTime - currentTime, 0);
        } catch (ExpiredJwtException e) {
            return 0;
        } catch (Exception e) {
            return -1;
        }
    }

    public long getExpiration(String token) {
        return getClaimsFromToken(token).getExpiration().getTime();
    }

    public boolean isRefreshTokenExpired(String token) {
        return getExpiration(token) <= System.currentTimeMillis();
    }

    public boolean isTokenNearExpiry(String token) {
        long remainingTime = getExpiration(token) - System.currentTimeMillis();
        return remainingTime <= NEAR_EXPIRY_BUFFER_TIME;
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}
