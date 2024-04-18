package com.t3t.apigateway.exception;

/**
 * 토큰이 올바르게 구성되지 않은 경우 에러 발생
 * @author joohyun1996(이주현)
 */
public class TokenNotConsistedProperly extends RuntimeException{
    public TokenNotConsistedProperly(String s) {
        super(s);
    }
}
