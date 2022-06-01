package com.example.demo.config;

import javax.persistence.EntityNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.AllArgsConstructor;
import lombok.val;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

	@AllArgsConstructor
	public static class EntityNotFoundErrorResponse {
		public String exceptionType;
		public String errorMessage;
	}
	
    @ExceptionHandler(EntityNotFoundException.class)
    private ResponseEntity<EntityNotFoundErrorResponse> handleEntityNotFound(EntityNotFoundException ex){
        val error = new EntityNotFoundErrorResponse("EntityNotFoundException", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

}

