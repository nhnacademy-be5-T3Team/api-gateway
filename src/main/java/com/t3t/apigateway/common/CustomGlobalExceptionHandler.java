package com.t3t.apigateway.common;

import com.t3t.apigateway.exception.BlackListTokenExceptions;
import com.t3t.apigateway.exception.TokenExpiredExceptions;
import com.t3t.apigateway.exception.TokenNotAuthenticatedExceptions;
import com.t3t.apigateway.exception.TokenNotExistExceptions;
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

    // error response 시 status와 message만 반환하도록 custom
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
        /*if (throwable instanceof ResponseStatusException) {
            return ((ResponseStatusException) throwable).getStatusCode();
        } else if (throwable instanceof CustomRequestAuthException) {
            return HttpStatus.UNAUTHORIZED;
        } else if (throwable instanceof RateLimitRequestException) {
            return HttpStatus.TOO_MANY_REQUESTS;
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }*/
    }
}
