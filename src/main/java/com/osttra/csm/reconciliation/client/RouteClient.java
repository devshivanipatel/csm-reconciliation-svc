package com.osttra.csm.reconciliation.client;

import com.osttra.csm.reconciliation.entity.CacheStatusResult;
import com.osttra.csm.reconciliation.util.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


import java.net.URI;
import java.util.*;

@Service
@Slf4j
public class RouteClient {

    @Autowired
    private  RestTemplate restTemplate;
    @Value("${route.host}")
    private String routeHost;
    @Value("${route.cacheUri}")
    private String routeCacheUri;

    @Autowired
    private JWTUtil jwtUtil;



    public List<CacheStatusResult> getCacheKeysStatus(List<String> keys) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(routeHost)
                .path(routeCacheUri);


        if (keys != null && !keys.isEmpty()) {
            for (String key : keys) {
                uriBuilder.queryParam("keys", key);
            }
        } else {
            log.warn("Attempting to call getCacheKeysStatus with an empty or null list of keys. Returning empty list.");
            return Collections.emptyList();
        }

        URI uri = uriBuilder.build().encode().toUri(); // Build and encode the URI

        log.info("Request URL: {}", uri.toString());

        // --- 2. Make the API call ---
        var headers = jwtUtil.getCognitoHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        List<CacheStatusResult> results ;

        try {
            HttpEntity<?> request = new HttpEntity<>(keys, headers);

            results = restTemplate.exchange(uri,HttpMethod.GET,request,new ParameterizedTypeReference<List<CacheStatusResult>>(){}).getBody();

            if (results != null) {
                log.info("Received {} cache status results from API.", results.size());
                return results;
            } else {
                log.warn("Route cache API returned null body for requestURL: {}", uri.toString());
                return Collections.emptyList();
            }

        } catch (Exception e) {
            log.error("Error calling route cache API for keys: {}. Error: {}", keys, e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}

