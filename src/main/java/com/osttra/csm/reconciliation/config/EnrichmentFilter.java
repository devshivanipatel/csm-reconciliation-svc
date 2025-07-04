package com.osttra.csm.reconciliation.config;

import com.osttra.csm.reconciliation.exception.TokenValidationException;
import com.osttra.csm.reconciliation.util.JWTUtil;
import com.osttra.csm.cognito.CognitoJwtValidator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static com.osttra.csm.reconciliation.constants.MapContextHolderCons.*;

/**
 * This class provide JWT filters which need to be checked on any incoming http
 * request
 */
@Slf4j
public class EnrichmentFilter extends OncePerRequestFilter {

    public static final String COGNITO_IDP = "cognito-idp";
    private final JWTUtil jwtUtil;
    private final AntPathMatcher matcher = new AntPathMatcher();
    @Value("${jwt.excluded.urls}")
    private List<String> excludedUrlPatterns;
    @Value("${app.user.uid}")
    private String uid;
    @Value("${app.user.oid}")
    private String oid;

    private CognitoJwtValidator cognitoJWTValidator;

    public EnrichmentFilter(JWTUtil jwtUtil, CognitoJwtValidator cognitoJWTValidator) {
        this.jwtUtil = jwtUtil;
        this.cognitoJWTValidator = cognitoJWTValidator;
    }

    /**
     * @param filterChain This method validate the jwt token and then set the
     *                    Authentication Context Holder and JWT Token Context Holder
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {

        String authToken = resolveToken(request);

        String token = getToken(fromRequestHeader(request, HttpHeaders.AUTHORIZATION));
        String rolePermissions = fromRequestHeader(request, USER_PERMISSIONS_HEADER);
        List<String> userRoles = rolePermissions != null ? Arrays.asList(rolePermissions.split(",")) : List.of();
        final String roles = fromRequestHeader(request, USER_ROLES_HEADER);
        final String roleTypes = fromRequestHeader(request, USER_ROLES_TYPE_HEADER);
        final String orgId = fromRequestHeader(request, ORG_ID_HEADER);
        final String userId = fromRequestHeader(request, USER_ID_HEADER);

        log.debug("USER_PERMISSIONS_HEADER:" + rolePermissions);
        log.debug("TOKEN:" + token);
        log.debug("TOKEN TYPE:" + authToken);
        log.debug("ORG_ID_HEADER:" + orgId);
        log.debug("USER_ID_HEADER:" + userId);
        log.debug("USER_ROLES_TYPE_HEADER:" + roleTypes);
        log.debug("USER_ROLES_HEADER:" + roles);

        if (JWTUtil.COGNITO_TOKEN.equals(authToken)) {
            log.info("Forwarding Request to COGNITO FILTER: {}", request.getHeader(HttpHeaders.AUTHORIZATION));

            try {
                MapContextHolder.addContext(JWT_TOKEN, null);
                MapContextHolder.addContext(COGNITO_TOKEN, token);
                setupMapContextHolder(rolePermissions, oid, uid, roleTypes, roles);

                Map<String, Object> claims = cognitoJWTValidator.getClaimsFromToken(token);
                if (claims != null) {
                    log.debug("Creating Authentication from generated user permissions");
                    Authentication authentication = this.cognitoJWTValidator.getAuthenticationWithRoles((String) claims.get("client_id"), userRoles);
                    log.debug("Setting authentication security context");
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("Security Context updated sucessfully.");
                } else {
                    log.debug("Claims from token are empty.");
                    log.debug("Security Context not updated.");
                }

                filterChain.doFilter(request, response);
            } catch (Exception e) {
                log.error("Exception doFilterInternal ", e);
            } finally {
                log.debug("cognito authToken reset");
                MapContextHolder.clearContext();
            }

            resetAuthenticationAfterRequest();
        } else if (JWTUtil.JWT_TOKEN.equals(authToken)) {
            log.debug("jwtStore authToken set");
            Authentication authentication = this.jwtUtil.getAuthenticationWithRoles(token, userRoles);
            /**
             * Setting the authentication to security context holder
             */
            SecurityContextHolder.getContext().setAuthentication(authentication);

            try {
                MapContextHolder.addContext(JWT_TOKEN, token);
                MapContextHolder.addContext(COGNITO_TOKEN, null);
                setupMapContextHolder(rolePermissions, orgId, userId, roleTypes, roles);

                filterChain.doFilter(request, response);
            } catch (Exception e) {
                log.error("Exception doFilterInternal ", e);
            } finally {
                log.debug("jwtStore authToken reset1");
                MapContextHolder.clearContext();
            }

            resetAuthenticationAfterRequest();
        } else {
            log.info("Auth Token is null. UNAUTHORIZED Access");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private static void setupMapContextHolder(String rolePermissions, String orgId, String userId, String roleTypes, String roles) {
        MapContextHolder.addContext(USER_PERMISSIONS_HEADER, rolePermissions);
        MapContextHolder.addContext(ORG_ID_HEADER, orgId);
        MapContextHolder.addContext(USER_ID_HEADER, userId);
        MapContextHolder.addContext(USER_ROLES_TYPE_HEADER, roleTypes);
        MapContextHolder.addContext(USER_ROLES_HEADER, roles);
    }

    /**
     * @return Bearer token
     */
    private String resolveToken(HttpServletRequest exchange) {
        String tokenType = JWTUtil.JWT_TOKEN;
        final String token = fromRequestHeader(exchange, HttpHeaders.AUTHORIZATION);
        log.debug("Authorization header: {}", token);
        if (token != null) {
            try {
                String[] parts = getToken(token).split("\\.");
                if (parts.length != 3) {
                    throw new IllegalArgumentException("Invalid JWT token format");
                }
                // Decode the payload (2nd part)
                String payloadJson = new String(Base64.getDecoder().decode(parts[1]), StandardCharsets.UTF_8);

                if (payloadJson.contains(COGNITO_IDP)) {
                    tokenType = JWTUtil.COGNITO_TOKEN;
                }
            } catch (Exception e) {
                throw new TokenValidationException("Invalid token: " + e.getMessage());
            }
        }

        log.debug("tokenType : {}", tokenType);
        return tokenType;
    }

    private String getToken(String bearerToken) {
        if (bearerToken.startsWith(JWTUtil.AUTH_TOKEN_PREFIX)) {
            return bearerToken.substring(7);
        } else {
            return bearerToken;
        }
    }

    /**
     * Clearing the JWT context holder
     */
    @Override
    public void destroy() {
        log.debug("jwtStore authToken reset destroy");
        MapContextHolder.clearContext();

        super.destroy();
    }

    /**
     * Resetting the SecurityContextHolder to the default null
     */
    private void resetAuthenticationAfterRequest() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return excludedUrlPatterns.stream().anyMatch(p -> matcher.match(p, request.getServletPath()));
    }

    private String fromRequestHeader(HttpServletRequest request, String key) {
        String value = "";
        try {
            value = request.getHeader(key);

        } catch (Exception e) {
            log.error("Error while fetching {} from the request header: {}", key, e.getMessage());
        }

        return value;
    }

}
