package edu.escuelaing.techcup.shared.security;

import edu.escuelaing.techcup.shared.config.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Issues and verifies stateless HS256 JSON Web Tokens.
 * Claims: {@code sub} = user id, {@code email}, {@code roles}. Tokens are not stored server side;
 * logout is a client-side discard (documented trade-off in ARCHITECTURE.md).
 */
@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);
    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_ROLES = "roles";

    private final SecretKey key;
    private final Duration expiration;

    public JwtService(AppProperties properties) {
        this.key = Keys.hmacShaKeyFor(properties.jwt().secret().getBytes(StandardCharsets.UTF_8));
        this.expiration = Duration.ofMinutes(properties.jwt().expirationMinutes());
    }

    /** A freshly issued token and its absolute expiry. */
    public record IssuedToken(String token, Instant expiresAt) {
    }

    /** The verified content of a token. */
    public record TokenClaims(Long userId, String email, List<String> roles) {
    }

    public IssuedToken issue(Long userId, String email, Collection<String> roles) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(expiration);
        String token = Jwts.builder()
                .subject(String.valueOf(userId))
                .claim(CLAIM_EMAIL, email)
                .claim(CLAIM_ROLES, List.copyOf(roles))
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(key)
                .compact();
        return new IssuedToken(token, expiresAt);
    }

    /** Verifies signature and expiry; returns empty for any malformed, tampered or expired token. */
    public Optional<TokenClaims> parse(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            Long userId = Long.valueOf(claims.getSubject());
            String email = claims.get(CLAIM_EMAIL, String.class);
            @SuppressWarnings("unchecked")
            List<String> roles = claims.get(CLAIM_ROLES, List.class);
            return Optional.of(new TokenClaims(userId, email, roles == null ? List.of() : roles));
        } catch (JwtException | IllegalArgumentException ex) {
            log.debug("Rejected JWT: {}", ex.getMessage());
            return Optional.empty();
        }
    }
}
