package com.t3t.apigateway.exception;

public class BlackListTokenExceptions extends RuntimeException{
    public BlackListTokenExceptions(String s) {
        super(s);
    }
}
