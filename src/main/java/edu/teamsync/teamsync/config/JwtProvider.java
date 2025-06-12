package edu.teamsync.teamsync.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class JwtProvider {
    private final SecretKey key;

    public JwtProvider(JwtConstant jwtConstant) {
        this.key = Keys.hmacShaKeyFor(jwtConstant.getSecretKey().getBytes());
    }

    public String generateToken(Authentication auth) {
        // Build JWT token
        String jwt = Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + 12000000))
                .claim("email", auth.getName())
                .claim("authorities", auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(",")))
                .signWith(key)
                .compact();

        return jwt;
    }

//    public String getEmailFromToken(String jwt) {
//        jwt = jwt.substring(7);
//        return Jwts.parserBuilder()
//                .setSigningKey(key)
//                .build()
//                .parseClaimsJws(jwt)
//                .getBody()
//                .get("email", String.class);
//    }
}

