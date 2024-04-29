package com.t3t.apigateway.keymanager.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;

/**
 * Secret Key Manager 에서 사용될 속성을 저장하는 프로퍼티 클래스
 * @author woody35545(구건모)
 */
@Getter
@Setter
@Profile("!local")
@ConfigurationProperties(prefix = "t3t.secret-key-manager")
public class SecretKeyManagerProperties {
    private String appKey;
    private String password;
    private String certKeyType;
    private String certKeyPath;
    @Value("${t3t.secretKeyManager.certKeyPath}")
    private Resource certKey;
}