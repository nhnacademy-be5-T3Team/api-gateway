package com.t3t.apigateway.exception;

public class TokenNotAuthenticatedExceptions extends RuntimeException{
    public TokenNotAuthenticatedExceptions(String s) {
        super(s);
    }
}
