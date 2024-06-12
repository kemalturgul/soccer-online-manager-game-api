package com.turgul.soccer.manager.exception;

import com.turgul.soccer.manager.dto.response.ErrorBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice("com.turgul.soccer.manager.controller")
public class GlobalApiExceptionHandler extends ResponseEntityExceptionHandler {

  @ResponseBody
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorBody> handleConstraintViolationException(
      HttpServletRequest request, ConstraintViolationException ex) {
    log.warn(
        "Request processing failed for method:{}, path:{}",
        request.getMethod(),
        request.getRequestURI(),
        ex);
    return new ResponseEntity<>(new ErrorBody(ex.getMessage()), HttpStatus.BAD_REQUEST);
  }

  @ResponseBody
  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ErrorBody> handleResponseStatusException(
      HttpServletRequest request, ResponseStatusException ex) {
    log.warn(
        "Request processing failed for method:{}, path:{}",
        request.getMethod(),
        request.getRequestURI(),
        ex);
    return new ResponseEntity<>(new ErrorBody(ex.getMessage()), ex.getStatusCode());
  }
}
