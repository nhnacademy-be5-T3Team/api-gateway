package com.t3t.apigateway.keymanager.service;

import com.t3t.apigateway.keymanager.exception.SecretKeyManagerApiRequestFailedException;
import com.t3t.apigateway.keymanager.model.response.SecretKeyManagerResponse;
import com.t3t.apigateway.keymanager.properties.SecretKeyManagerProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Secret Key Manager 에 등록된 Secret 값을 가져오기 위한 서비스 클래스
 *
 * @author woody35545(구건모)
 */
@Profile("!local")
@Slf4j
@Service
@RequiredArgsConstructor
public class SecretKeyManagerService {
    private final RestTemplate sslRestTemplate;
    private final SecretKeyManagerProperties secretKeyManagerProperties;

    /**
     * Secret Key Manager 에서 Secret 값 조회
     *
     * @param keyId 조회할 Key ID(Secret Key Manager 에 등록된 기밀 데이터의 Key ID)
     * @return Secret Key Manager 에서 조회한 Secret 값을 String 형태로 반환
     * @author woody35545(구건모)
     */
    public String getSecretValue(String keyId) {

        HttpEntity<SecretKeyManagerResponse> response = sslRestTemplate.exchange("https://api-keymanager.nhncloudservice.com/keymanager/v1.0/appkey/{appKey}/secrets/{keyId}",
                HttpMethod.GET, null, SecretKeyManagerResponse.class, secretKeyManagerProperties.getAppKey(), keyId);

        SecretKeyManagerResponse responseBody = response.getBody();

        if (responseBody == null) {
            throw new SecretKeyManagerApiRequestFailedException("Response body is null.");
        }

        if (responseBody.getHeader() == null || responseBody.getBody() == null ||
                !responseBody.getHeader().getIsSuccessful().equals("true") || responseBody.getBody().getSecret() == null) {
            log.error("Secret Key Manager API response: {}", responseBody);
            throw new SecretKeyManagerApiRequestFailedException(new StringBuilder().append("Fail to request Secret Key Manager API (Key ID:").append(keyId).append(")").toString());
        }

        return responseBody.getBody().getSecret();
    }
}