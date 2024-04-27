package com.t3t.apigateway.keymanager.exception;

/**
 * Secret Key Manager API 요청이 실패한 경우 발생하는 예외
 */
public class SecretKeyManagerApiRequestFailedException extends RuntimeException{
    public SecretKeyManagerApiRequestFailedException(String message) {
        super(message);
    }
}