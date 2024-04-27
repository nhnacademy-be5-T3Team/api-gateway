package com.t3t.apigateway.keymanager.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Secret Key Manager 에 등록된 기밀 데이터의 key id를 저장하는 프로퍼티 클래스
 *
 * @author woody35545(구건모)
 */
@Profile("!local")
@Getter
@Component
public class SecretKeyProperties {
    @Value("${t3t.secretKeyManager.secrets.databaseServerIpAddress.keyId}")
    private String databaseIpAddressKeyId;
    @Value("${t3t.secretKeyManager.secrets.databaseServerPort.keyId}")
    private String databasePortKeyId;
    @Value("${t3t.secretKeyManager.secrets.databaseServerUsername.keyId}")
    private String databaseNameKeyId;
    @Value("${t3t.secretKeyManager.secrets.databaseName.keyId}")
    private String databaseUsernameKeyId;
    @Value("${t3t.secretKeyManager.secrets.databaseServerPassword.keyId}")
    private String databasePasswordKeyId;
    @Value("${t3t.secretKeyManager.secrets.jwtSecretKey.keyId}")
    private String jwtSecretKeyId;
    @Value("${t3t.secretKeyManager.secrets.redisServerIpAddress.keyId}")
    private String redisIpAddressKeyId;
    @Value("${t3t.secretKeyManager.secrets.redisServerPort.keyId}")
    private String redisPortKeyId;
    @Value("${t3t.secretKeyManager.secrets.redisServerPassword.keyId}")
    private String redisPasswordKeyId;
}