package com.bytebandit.gateway.service;

import com.bytebandit.gateway.exception.InvalidTokenException;
import com.bytebandit.gateway.model.TokenEntity;
import com.bytebandit.gateway.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import lib.user.enums.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    @Value("${jwt.secret}")
    private String secretKey;

    private final TokenRepository tokenRepository;

    /**
     * This method generates a JWT token for the given user with the specified expiration time.
     */
    public String generateToken(
        UserDetails user,
        long expirationTimeInSeconds,
        UUID userId
    ) {
        List<String> authorities = user.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .toList();

        return Jwts.builder()
            .setSubject(user.getUsername())
            .claim("userid", userId)
            .claim("authorities", authorities)
            .setIssuedAt(new java.util.Date())
            .setExpiration(new java.util.Date(
                System.currentTimeMillis() + expirationTimeInSeconds * 1000)
            )
            .signWith(getSignInKey())
            .compact();
    }

    /**
     * This method generates a Refresh token for the given user
     * with the specified expiration time and access token.
     */
    public void generateAndSaveRefreshToken(
        UserDetails user,
        long expirationTimeInSeconds,
        String previousAccessToken
    ) {
        UUID userId = extractUserId(previousAccessToken);
        if (userId == null) {
            throw new InvalidTokenException("Token is invalid");
        }

        TokenEntity tokenEntity = tokenRepository.findByUserIdAndType(
                userId,
                TokenType.REFRESH
            ).orElseThrow(() -> new InvalidTokenException("Token is invalid"));

        tokenEntity.setTokenHash(generateToken(user, expirationTimeInSeconds, userId));
        tokenEntity.setExpiresAt(
            new Timestamp(System.currentTimeMillis() + expirationTimeInSeconds * 1000)
        );
        tokenRepository.save(tokenEntity);
    }

    /**
     * This method extracts the username from the JWT token.
     */
    public boolean isValidToken(String token, UserDetails user) {
        final String username = extractUsername(token);
        return (username.equals(user.getUsername())
            && !isTokenExpired(token));
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    /**
     * This method extracts all claims from the JWT token.
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSignInKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    /**
     * This method extracts the user ID from the JWT token.
     */
    public UUID extractUserId(String token) {
        return extractClaim(
            token,
            claims -> UUID.fromString(claims.get("userid", String.class))
        );
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }
}
