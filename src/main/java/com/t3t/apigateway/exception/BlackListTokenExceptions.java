package com.t3t.apigateway.exception;

/**
 * logout된 토큰으로 접근하는지 확인해서 예외를 발생시키는 클래스
 * @author joohyun1996(이주현)
 */
public class BlackListTokenExceptions extends RuntimeException{
    public BlackListTokenExceptions(String s) {
        super(s);
    }
}
