package farming.jwt.service;

import farming.jwt.dto.JwtTokenDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private static final Integer TOKEN_EXPIRATION = 1000 * 60 * 60;
    private static final Integer REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24;


    @Value("${token.secret}")
    private String tokenSecret;


    @Value("${token.expiration.hours}")
    private Integer tokenExpireHours;

    /**
     * Generate token with given user name
     *
     * @param userName
     * @return Token
     */
    public JwtTokenDTO generateToken(String userName) {
        Map<String, Object> claims = new HashMap<>();
        return generateTokens(userName, claims);
    }

    /**
     * Extract the username from the token
     *
     * @param token
     * @return UserName
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract the expiration date from the token
     *
     * @param token
     * @return expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract a claim from the token
     *
     * @param token
     * @param claimsResolver
     * @param <T>
     * @return Claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Validate the token against user details and expiration
     *
     * @param token
     * @param userDetails
     * @return Boolean
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Create a JWT token with specified claims and subject (user name)
     *
     * @param claims
     * @param userName
     * @return Token
     */
    private String createToken(Map<String, Object> claims, String userName, long expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date())
                .setExpiration(new Date(expiration))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public JwtTokenDTO generateTokens(String userName, Map<String, Object> claims) {
        long accessTokenExpiration = System.currentTimeMillis() + ((long) TOKEN_EXPIRATION * tokenExpireHours);
        long refreshTokenExpiration = System.currentTimeMillis() + ((long) REFRESH_TOKEN_EXPIRATION * 7);

        // Generate access and refresh tokens
        String accessToken = createToken(claims, userName, accessTokenExpiration);
        String refreshToken = createToken(new HashMap<>(), userName, refreshTokenExpiration);

        return new JwtTokenDTO(accessToken, refreshToken);
    }

    /**
     * Get the signing key for JWT token
     *
     * @return Key
     */
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(tokenSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extract all claims from the token
     *
     * @param token
     * @return Claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
    }

    /**
     * Check if the token is expired
     *
     * @param token
     * @return Boolean
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

}
