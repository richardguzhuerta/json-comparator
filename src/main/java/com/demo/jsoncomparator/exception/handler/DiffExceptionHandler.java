package com.demo.jsoncomparator.exception.handler;

import com.demo.jsoncomparator.exception.DiffException;
import com.demo.jsoncomparator.exception.MalFormedJsonException;
import com.demo.jsoncomparator.exception.NoDataFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.stream.Collectors.joining;

/**
 * Handles exceptions in a centralized manner
 *
 * @author Richard Guzman
 * @version v1, March 2021
 */
@ControllerAdvice
public class DiffExceptionHandler {

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<Object> handler(WebExchangeBindException validException) {
        return new ResponseEntity<>(
                buildResponseBody(LocalDateTime.now(),
                        validException.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(joining(" , "))
                ), HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handler(HttpMessageNotReadableException exception) {
        return new ResponseEntity<>(
                buildResponseBody(LocalDateTime.now(), HttpStatus.BAD_REQUEST.getReasonPhrase()), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(NoDataFoundException.class)
    public ResponseEntity<Object> handler(NoDataFoundException noDataFoundException) {
        return new ResponseEntity<>(buildResponseBody(LocalDateTime.now(),
                noDataFoundException.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MalFormedJsonException.class)
    public ResponseEntity<Object> handler(MalFormedJsonException malFormedJsonException) {
        return new ResponseEntity<>(buildResponseBody(LocalDateTime.now(),
                malFormedJsonException.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DiffException.class)
    public ResponseEntity<Object> handler(DiffException diffException) {
        return new ResponseEntity<>(buildResponseBody(LocalDateTime.now(),
                diffException.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private Map<String, Object> buildResponseBody(LocalDateTime timestamp, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", timestamp);
        body.put("message", message);
        return body;
    }
}
