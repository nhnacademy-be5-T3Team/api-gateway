package com.t3t.apigateway.exception;

public class TokenExpiredExceptions extends RuntimeException{
    public TokenExpiredExceptions(String s) {
        super(s);
    }
}
