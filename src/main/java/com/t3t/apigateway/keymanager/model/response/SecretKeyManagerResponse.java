package com.t3t.apigateway.keymanager.model.response;

import lombok.Getter;

/**
 * Secret Key Manager API의 응답 형식을 정의한 클래스
 *
 * @author woody35545(구건모)
 */
@Getter
public class SecretKeyManagerResponse {
    private SecretKeyManagerResponseHeaderPartDto header;
    private SecretKeyManagerResponseBodyPartDto body;

    @Getter
    public static class SecretKeyManagerResponseHeaderPartDto {
        private int resultCode;
        private String resultMessage;
        private String isSuccessful;
    }

    @Getter
    public static class SecretKeyManagerResponseBodyPartDto {
        private String secret;
    }
}