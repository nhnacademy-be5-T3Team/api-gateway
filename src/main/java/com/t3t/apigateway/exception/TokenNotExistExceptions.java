package com.t3t.apigateway.exception;

/**
 * 토큰이 없는경우 에러 발생
 *
 */
public class TokenNotExistExceptions extends RuntimeException{
    public TokenNotExistExceptions(String s) {
        super(s);
    }
}
