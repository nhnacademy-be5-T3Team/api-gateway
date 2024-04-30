package com.t3t.apigateway.keymanager;


import com.t3t.apigateway.keymanager.properties.SecretKeyProperties;
import com.t3t.apigateway.keymanager.service.SecretKeyManagerService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Secret Key Manager API 를 통해 기밀 데이터를 정상적으로 가져오는지에 대한 통합 테스트한다.<br>
 * 해당 테스트는 가져온 기밀데이터가 실제값과 동일한지 확인하는 테스트가 아니라,<br>
 * Secret Key Manager API 를 통해 값이 조회되는지에 대한 테스트이다.<br>
 * Secret Key Manager 빈을 위한 실행 환경 변수를 설정하고, application.yml 파일을 통해 테스트에 사용할 key id 를 정의하여 테스트한다.<br>
 *
 * @author woody35545(구건모)
 * @apiNote 확인이 필요한 경우에만 실행하도록 설정하기 위해 테스트를 하고자하는 항목을 제외하고는 @Disabled 어노테이션을 선언하여 테스트를 비활성화한다.<br>
 * @see SecretKeyManagerService
 * @see SecretKeyProperties
 */
@Slf4j
@SpringBootTest
class SecretKeyManagerTest {

    @Autowired
    private SecretKeyProperties secretKeyProperties;

    @Autowired
    private SecretKeyManagerService secretKeyManagerService;


    /**
     * Secret Key Manager API를 통해 databaseIpAddress 값이 정상적으로 로드되는지 테스트
     *
     * @author woody35545(구건모)
     */
    @Test
//    @Disabled
    void databaseIpAddressLoadTest() {

        // when & then
        Assertions.assertDoesNotThrow(() -> secretKeyManagerService.getSecretValue(secretKeyProperties.getDatabaseIpAddressKeyId()));
        String value = secretKeyManagerService.getSecretValue(secretKeyProperties.getDatabaseIpAddressKeyId());
        Assertions.assertNotNull(value);

        log.info("databaseIpAddress => {}", value);
    }

    /**
     * Secret Key Manager API를 통해 databasePort 값이 정상적으로 로드되는지 테스트
     *
     * @author woody35545(구건모)
     */
    @Test
//    @Disabled
    void databasePortLoadTest() {

        // when & then
        Assertions.assertDoesNotThrow(() -> secretKeyManagerService.getSecretValue(secretKeyProperties.getDatabasePortKeyId()));
        String value = secretKeyManagerService.getSecretValue(secretKeyProperties.getDatabasePortKeyId());
        Assertions.assertNotNull(value);

        log.info("databasePort => {}", value);
    }

    /**
     * Secret Key Manager API를 통해 databaseName 값이 정상적으로 로드되는지 테스트
     *
     * @author woody35545(구건모)
     */
    @Test
//    @Disabled
    void databaseNameLoadTest() {

        // when & then
        Assertions.assertDoesNotThrow(() -> secretKeyManagerService.getSecretValue(secretKeyProperties.getDatabaseNameKeyId()));
        String value = secretKeyManagerService.getSecretValue(secretKeyProperties.getDatabaseNameKeyId());
        Assertions.assertNotNull(value);

        log.info("databaseName => {}", value);
    }

    /**
     * Secret Key Manager API를 통해 databaseUsername 값이 정상적으로 로드되는지 테스트
     *
     * @author woody35545(구건모)
     */
    @Test
//    @Disabled
    void databaseUsernameLoadTest() {

        // when & then
        Assertions.assertDoesNotThrow(() -> secretKeyManagerService.getSecretValue(secretKeyProperties.getDatabaseUsernameKeyId()));
        String value = secretKeyManagerService.getSecretValue(secretKeyProperties.getDatabaseUsernameKeyId());
        Assertions.assertNotNull(value);

        log.info("databaseUsername => {}", value);
    }


    /**
     * Secret Key Manager API를 통해 databasePassword 값이 정상적으로 로드되는지 테스트
     *
     * @author woody35545(구건모)
     */
    @Test
//    @Disabled
    void databasePasswordLoadTest() {
        Assertions.assertDoesNotThrow(() -> secretKeyManagerService.getSecretValue(secretKeyProperties.getDatabasePasswordKeyId()));
        String value = secretKeyManagerService.getSecretValue(secretKeyProperties.getDatabasePasswordKeyId());

        Assertions.assertNotNull(value);
    }


    /**
     * Secret Key Manager API를 통해 jwtSecretKey 값이 정상적으로 로드되는지 테스트
     *
     * @author woody35545(구건모)
     */
    @Test
//    @Disabled
    void jwtSecretKeyLoadTest() {
        Assertions.assertDoesNotThrow(() -> secretKeyManagerService.getSecretValue(secretKeyProperties.getJwtSecretKeyId()));
        String value = secretKeyManagerService.getSecretValue(secretKeyProperties.getJwtSecretKeyId());
        log.info(value);
        log.info(value.toString());

        Assertions.assertNotNull(value);
    }

    /**
     * Secret Key Manager API 를 통해 redisIpAddress 값이 정상적으로 로드되는지 테스트
     *
     * @author woody35545(구건모)
     */
    @Test
//    @Disabled
    void redisIpAddressLoadTest() {
        Assertions.assertDoesNotThrow(() -> secretKeyManagerService.getSecretValue(secretKeyProperties.getRedisIpAddressKeyId()));

        String value = secretKeyManagerService.getSecretValue(secretKeyProperties.getRedisIpAddressKeyId());
        Assertions.assertNotNull(value);


        log.info(value);

        log.info("redisIpAddress => {}", value);
    }

    /**
     * Secret Key Manager API 를 통해 redisPort 값이 정상적으로 로드되는지 테스트
     *
     * @author woody35545(구건모)
     */
    @Test
//    @Disabled
    void redisPortLoadTest() {
        Assertions.assertDoesNotThrow(() -> secretKeyManagerService.getSecretValue(secretKeyProperties.getRedisPortKeyId()));

        String value = secretKeyManagerService.getSecretValue(secretKeyProperties.getRedisPortKeyId());
        Assertions.assertNotNull(value);

        log.info("redisPort => {}", value);
    }

    /**
     * Secret Key Manager API 를 통해 redisPassword 값이 정상적으로 로드되는지 테스트
     *
     * @author woody35545(구건모)
     */
    @Test
//    @Disabled
    void redisPasswordLoadTest() {
        Assertions.assertDoesNotThrow(() -> secretKeyManagerService.getSecretValue(secretKeyProperties.getRedisPasswordKeyId()));

        String value = secretKeyManagerService.getSecretValue(secretKeyProperties.getRedisPasswordKeyId());
        Assertions.assertNotNull(value);
    }
}