package com.t3t.apigateway.exception;

/**
 * access 혹은 refresh 토큰이 만료됬을 경우 에러 발생
 * @author joohyun1996(이주현)
 */
public class TokenExpiredExceptions extends RuntimeException{
    public TokenExpiredExceptions(String s) {
        super(s);
    }
}
