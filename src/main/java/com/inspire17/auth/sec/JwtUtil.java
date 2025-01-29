package com.inspire17.auth.sec;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    @Value("${spring.jwt.secret}")
    private String secretKey;

    @Value("${spring.jwt.token-validity}")
    private long jwtExpiration;

    private Key key;


    // Initialize the key after dependency injection
    @PostConstruct
    public void init() {
        this.key = produceKey();
    }

    // Generate the HMAC key from the secret key
    private Key produceKey() {
        if (secretKey == null) {
            throw new IllegalStateException("Secret key is null. Check if it is properly configured in application-dev.properties.");
        }
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        extraClaims.put("authorities", userDetails.getAuthorities());
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }


    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String refreshToken(String token) {
        // Validate old token
        if (isTokenExpired(token)) {
            throw new IllegalArgumentException("Refresh token expired. Please log in again.");
        }

        String username = extractUsername(token);
        Map<String, Object> claims = extractAllClaims(token);

        Collection<? extends GrantedAuthority> authorities = extractGrantedAuthorities(token);

        UserDetails userDetails = new User(username, "", authorities);

        return buildToken(claims, userDetails, jwtExpiration);

    }

    private Collection<? extends GrantedAuthority> extractGrantedAuthorities(String token) {
        Claims claims = extractAllClaims(token);

        List<?> authoritiesClaim = claims.get("authorities", List.class);

        if (authoritiesClaim == null) {
            return new ArrayList<>(); // Return empty list if no authorities found
        }

        return authoritiesClaim.stream()
                .map(entry -> new SimpleGrantedAuthority(((Map<String, String>) entry).get("authority")))
                .collect(Collectors.toList());
    }
}