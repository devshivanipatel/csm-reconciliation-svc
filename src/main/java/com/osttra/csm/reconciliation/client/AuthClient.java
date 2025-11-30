package com.osttra.csm.reconciliation.client;

import com.osttra.csm.domain.dto.LoginRequest;
import com.osttra.csm.domain.dto.LoginResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author sitanshu
 * <p>
 * This class is a client class to do HTTP operations related to AuthMS
 */
@Slf4j
@Service
public class AuthClient {

    @Value("${host.auth}")
    private String hostAuthProp;

    @Value("${admin.user}")
    private String username;

    @Value("${admin.password}")
    private String encodedPassword;

    @Autowired
    private RestTemplate restTemplate;

    public LoginResponse getAuthToken() {
        log.debug("Get Auth Token from Auth Service of user :" + username);
        final String reqUrl = hostAuthProp + "/login";
        log.debug("reqUrl:" + reqUrl);
        final HttpEntity<LoginRequest> reqEntity = new HttpEntity<>(new LoginRequest(username, encodedPassword));
        return restTemplate.exchange(reqUrl, HttpMethod.POST, reqEntity, LoginResponse.class).getBody();
    }
}
