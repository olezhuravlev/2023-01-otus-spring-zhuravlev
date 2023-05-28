package ru.otus.spring.exception.rest;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.otus.spring.component.ModelAndViewPopulator;

import java.util.*;

@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    ModelAndViewPopulator modelAndViewPopulator;

    @ExceptionHandler(value = {NoSuchElementException.class})
    protected ModelAndView handleException(NoSuchElementException exception, HttpServletRequest request) {
        return modelAndViewPopulator.fillError404(request, new ModelAndView());
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        headers.add("Location", "error");
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        Map<String, List<String>> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            List<String> fieldErrors = errors.getOrDefault(fieldName, new ArrayList<>());
            fieldErrors.add(error.getDefaultMessage());
            errors.put(fieldName, fieldErrors);
        });

        return new ResponseEntity<>(errors, HttpStatus.NOT_ACCEPTABLE);
    }
}
