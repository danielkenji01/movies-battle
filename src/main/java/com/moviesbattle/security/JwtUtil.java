package com.moviesbattle.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import com.moviesbattle.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class JwtUtil {

    private static final String SECRET_KEY = "04d0ff1d-0a47-4e02-a197-f18bd49ec245";

    public static String generateToken(String username) {
        final Date now = new Date();

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    public static String getUsernameFromToken(final String token) {
        final Claims claims = Jwts.parser()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getPayload();

        return claims.getSubject();
    }

    public static String getLoggedUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            return authentication.getName();
        }

        throw new UnauthorizedException();
    }

}
