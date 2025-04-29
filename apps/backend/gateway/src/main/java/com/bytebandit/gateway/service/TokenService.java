package com.bytebandit.gateway.service;

import com.bytebandit.gateway.exception.InvalidTokenException;
import com.bytebandit.gateway.model.TokenEntity;
import com.bytebandit.gateway.model.UserEntity;
import com.bytebandit.gateway.repository.TokenRepository;
import com.bytebandit.gateway.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import java.security.Key;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import lib.user.enums.TokenType;
import lib.user.model.TokenEntityTemplate;
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
    private final UserRepository userRepository;
    
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
     * This method generates a Refresh token for the given user with the specified expiration time
     * and access token.
     */
    @Transactional
    public void generateAndSaveRefreshToken(
        UserDetails user,
        long expirationTimeInSeconds,
        String previousAccessToken
    ) {
        UUID userId = extractUserId(previousAccessToken);
        if (userId == null) {
            throw new InvalidTokenException("Token is invalid");
        }
        
        // looking for a valid(not used) refresh token
        List<TokenEntity> tokenEntities =
            tokenRepository.findAllByUserIdAndTypeAndUsed(userId, TokenType.REFRESH, false);
        if (tokenEntities.isEmpty()) {
            throw new InvalidTokenException("Valid refresh token not found");
        }
        
        invalidateAllRefreshToken(userId);
        
        // create a new token
        tokenRepository.save(TokenEntity.builder()
            .tokenHash(generateToken(user, expirationTimeInSeconds, userId))
            .used(false)
            .type(TokenType.REFRESH)
            .expiresAt(new Timestamp(System.currentTimeMillis() + expirationTimeInSeconds * 1000))
            .user(userRepository.findById(userId).orElseThrow()).build());
    }
    
    /**
     * This method generates a Refresh token for the given user with the specified expiration time
     * and user ID.
     */
    @Transactional
    public void generateAndSaveRefreshToken(
        UserDetails user,
        long expirationTimeInSeconds,
        UUID userId
    ) {
        UserEntity userEntity = userRepository.findById(userId)
            .orElseThrow(() -> new InvalidTokenException("User not found"));
        
        invalidateAllRefreshToken(userId);
        TokenEntity tokenEntity = TokenEntity.builder()
            .user(userEntity)
            .type(TokenType.REFRESH)
            .used(false)
            .tokenHash(generateToken(user, expirationTimeInSeconds, userId))
            .expiresAt(new Timestamp(System.currentTimeMillis() + expirationTimeInSeconds * 1000))
            .build();
        
        tokenRepository.save(tokenEntity);
    }
    
    /**
     * Invalidate all refresh tokens for the given user ID.
     *
     * @param userId the user ID
     */
    @Transactional
    protected void invalidateAllRefreshToken(UUID userId) {
        List<TokenEntity> tokens =
            tokenRepository.findAllByUserIdAndTypeAndUsed(userId,
                TokenType.REFRESH, false).stream().toList();
        for (TokenEntity token : tokens) {
            token.setUsed(true);
            token.setExpiresAt(new Timestamp(System.currentTimeMillis()));
        }
        tokenRepository.saveAll(tokens);
    }
    
    /**
     * This method extracts the username from the JWT token.
     */
    public boolean isValidToken(String token, UserDetails user) {
        final String username = extractUsername(token);
        return (username.equals(user.getUsername())
            && !isTokenExpired(token));
    }
    
    public boolean isExpiredToken(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
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
        try {
            return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (SecurityException | MalformedJwtException e) {
            throw new InvalidTokenException("Invalid token signature");
        } catch (UnsupportedJwtException e) {
            throw new InvalidTokenException("Unsupported JWT token");
        } catch (IllegalArgumentException e) {
            throw new InvalidTokenException("JWT claims string is empty");
        }
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
