package com.t3t.apigateway.exception;

/**
 * 서버가 발행한 토큰이 아닐경우 예외발생
 * @author joohyun1996(이주현)
 */
public class TokenNotAuthenticatedExceptions extends RuntimeException{
    public TokenNotAuthenticatedExceptions(String s) {
        super(s);
    }
}
