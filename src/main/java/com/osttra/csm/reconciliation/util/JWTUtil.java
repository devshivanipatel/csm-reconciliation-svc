package com.osttra.csm.reconciliation.util;

import com.osttra.csm.cognito.CognitoUtility;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;

/**
 * Utility class to perform basic operations on JWT token
 */
@Slf4j
@Component
public class JWTUtil {

    public static final String AUTH_TOKEN_PREFIX = "Bearer ";
    public static final String CLAIM_UID = "uid";
    public static final String CLAIM_ORGID = "orgId";
    public static final String COGNITO_TOKEN = "Cognito_Token ";
    public static final String JWT_TOKEN = "Jwt_Token ";
    @Value("${secret.key.jwttoken}")
    private String secretKeyProp;
    @Value("${expiry.duration.jwttoken}")
    private Integer authTokenExpiryMilliSeconds;

    @Autowired
    CognitoUtility cognitoUtility;

    /**
     * @return userName Desc : extract userName from jwt token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * @return extract all claims set in the jwt token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secretKeyProp).parseClaimsJws(token).getBody();
    }

    /**
     * This method is used to create jwt token using claims and subject
     *
     * @param claims
     * @param subject
     * @return jwt token
     */
    public String createToken(Map<String, Object> claims, String subject) {

        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + authTokenExpiryMilliSeconds))
                .signWith(SignatureAlgorithm.HS256, secretKeyProp).compact();
    }

    /**
     * This method is used to create authentication using token and roles
     *
     * @return Authentication object consisting of principal and authorities
     */
    public Authentication getAuthenticationWithRoles(String token, List<String> permissions) {
        Claims claims = Jwts.parser().setSigningKey(secretKeyProp).parseClaimsJws(token).getBody();
        Collection<? extends GrantedAuthority> authorities = getAuthorities(permissions);
        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    /**
     * This method is used to create granted authorities from the list of
     * permissions
     */
    private Collection<? extends GrantedAuthority> getAuthorities(List<String> permissions) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String permission : permissions) {
            authorities.add(new SimpleGrantedAuthority(permission));
        }
        return authorities;
    }

    /**
     * This method is used to fetch the orgId of the user from the jwt token
     */
    public String extractOrgId(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKeyProp).parseClaimsJws(token).getBody();
        return claims.get(CLAIM_ORGID).toString();
    }

    /**
     * This method is used to extract uid of user from the token
     */
    public String extractUid(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKeyProp).parseClaimsJws(token).getBody();
        return claims.get(CLAIM_UID).toString();
    }

    public HttpHeaders getCognitoHeaders() {
        return cognitoUtility.getCognitoAccessHeaders();
    }
}
