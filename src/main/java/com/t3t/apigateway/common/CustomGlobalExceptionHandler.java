package com.t3t.apigateway.common;

import com.t3t.apigateway.exception.*;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Gateway Filter에서 발생하는 예외를 handling 하는클래스
 * @author joohyun1996(이주현)
 */
@Component
public class CustomGlobalExceptionHandler extends AbstractErrorWebExceptionHandler {
    public CustomGlobalExceptionHandler(final ErrorAttributes errorAttributes,
                                        final WebProperties.Resources resources,
                                        final ApplicationContext applicationContext,
                                        final ServerCodecConfigurer codecConfigurer) {
        super(errorAttributes, resources, applicationContext);
        setMessageReaders(codecConfigurer.getReaders());
        setMessageWriters(codecConfigurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    /**
     * error response 시 status와 message만 반환하도록 custom
     * @param request
     * @return StatusCode : message
     * @author joohyun1996(이주현)
     */
    private Mono<ServerResponse> renderErrorResponse(ServerRequest request){
        ErrorAttributeOptions options = ErrorAttributeOptions.of(ErrorAttributeOptions.Include.MESSAGE);
        Map<String, Object> errorPropertiesMap = getErrorAttributes(request, options);
        Throwable throwable = getError(request);
        HttpStatus httpStatus = determineHttpStatus(throwable);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("status", httpStatus.value());
        responseMap.put("message", errorPropertiesMap.get("message"));

        return ServerResponse.status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromObject(responseMap));
    }

    /**
     * 에러의 종류에 따라 status 설정
     * @author joohyun1996(이주현)
     */
    private HttpStatus determineHttpStatus(Throwable throwable) {
        if (throwable instanceof TokenNotExistExceptions){
            return HttpStatus.UNAUTHORIZED;
        }else if (throwable instanceof TokenExpiredExceptions){
            return HttpStatus.UNAUTHORIZED;
        }else if (throwable instanceof TokenNotAuthenticatedExceptions){
            return HttpStatus.FORBIDDEN;
        }else if (throwable instanceof BlackListTokenExceptions){
            return HttpStatus.FORBIDDEN;
        }else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
