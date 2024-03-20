package com.moviesbattle.exception;

import java.util.List;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<Object> processPlayerNotFoundException(final NotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    protected ResponseEntity<Object> processUnauthorizedException(final UnauthorizedException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @ExceptionHandler(MatchExistsException.class)
    protected ResponseEntity<Object> processMatchExistsException(final MatchExistsException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }

    @ExceptionHandler(PlayerAlreadyExistsException.class)
    protected ResponseEntity<Object> processPlayerAlreadyExistsException(final PlayerAlreadyExistsException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Player already exists");
    }

    @ExceptionHandler(AnswerNotValidException.class)
    protected ResponseEntity<Object> processAnswerNotValidException(final AnswerNotValidException exception) {
        return ResponseEntity.badRequest().body("Answer not valid");
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
            final HttpHeaders headers, final HttpStatusCode status, final WebRequest request) {
        final List<String> errors = ex.getBindingResult().getAllErrors().stream().map(
                DefaultMessageSourceResolvable::getDefaultMessage).toList();

        return ResponseEntity.badRequest().body(errors);
    }

}