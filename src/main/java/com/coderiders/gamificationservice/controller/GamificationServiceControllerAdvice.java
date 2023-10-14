package com.coderiders.gamificationservice.controller;

import com.coderiders.gamificationservice.exception.BadRequestException;
import com.coderiders.gamificationservice.exception.GamificationErrorResponse;
import com.coderiders.gamificationservice.exception.GamificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class GamificationServiceControllerAdvice {

    @ExceptionHandler(IllegalArgumentException.class)
    private ResponseEntity<GamificationErrorResponse> illegalArgumentExceptionHandler(GamificationException ex) {
        GamificationErrorResponse errorResponse = new GamificationErrorResponse();

        errorResponse.setErrorCode(HttpStatus.BAD_REQUEST.value());
        errorResponse.setErrorId(HttpStatus.BAD_REQUEST.getReasonPhrase());
        errorResponse.setErrorMessage(ex.getMessage());

        logException(ex, "illegalArgumentExceptionHandler");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    private ResponseEntity<GamificationErrorResponse> badRequestExceptionHandler(GamificationException ex) {
        GamificationErrorResponse errorResponse = new GamificationErrorResponse();

        errorResponse.setErrorCode(HttpStatus.BAD_REQUEST.value());
        errorResponse.setErrorId(HttpStatus.BAD_REQUEST.getReasonPhrase());
        errorResponse.setErrorMessage(ex.getMessage());

        logException(ex, "badRequestExceptionHandler");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(GamificationException.class)
    private ResponseEntity<GamificationErrorResponse> gamificationExceptionHandler(GamificationException ex) {
        GamificationErrorResponse errorResponse = new GamificationErrorResponse();

        errorResponse.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.setErrorId("ISE");
        errorResponse.setErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());

        logException(ex, "RecommendationException");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    private ResponseEntity<GamificationErrorResponse> runtimeExceptionHandler(RuntimeException ex) {
        GamificationErrorResponse errorResponse = new GamificationErrorResponse();

        errorResponse.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.setErrorId("ISE");
        errorResponse.setErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());

        logException(ex, "RuntimeException");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    private ResponseEntity<GamificationErrorResponse> exceptionHandler(Exception ex) {
        GamificationErrorResponse errorResponse = new GamificationErrorResponse();

        errorResponse.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.setErrorId("ISE");
        errorResponse.setErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());

        logException(ex, "Exception");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void logException(Exception ex, String exceptionType) {
        StringBuilder builder = new StringBuilder();
        builder.append(exceptionType).append(" occurred.");

        if (ex.getStackTrace().length > 0) {
            StackTraceElement ele = ex.getStackTrace()[0];
            builder.append("\nClass Name: ").append(ele.getClassName());
            builder.append("\nMethod Name: ").append(ele.getMethodName());
            builder.append("\nFile Name: ").append(ele.getFileName());
            builder.append("\nLine Number: ").append(ele.getLineNumber());

            log.error(builder.toString());
        }
    }
}
