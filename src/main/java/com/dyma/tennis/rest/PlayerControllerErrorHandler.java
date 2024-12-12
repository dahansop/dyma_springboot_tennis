package com.dyma.tennis.rest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.dyma.tennis.dto.Error;
import com.dyma.tennis.exceptions.PlayerAlreadyExistException;
import com.dyma.tennis.exceptions.PlayerDataRetrievalException;
import com.dyma.tennis.exceptions.PlayerNotFoundException;

/**
 * Gestion des erreurs du REST controller
 */
@RestControllerAdvice
public class PlayerControllerErrorHandler {

  @ExceptionHandler(PlayerNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public Error handlePlayerNotFoundException(PlayerNotFoundException ex) {
    return new Error(ex.getMessage());
  }
  
  @ExceptionHandler(PlayerAlreadyExistException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Error handlePlayerAlreadyExistException(PlayerAlreadyExistException ex) {
    return new Error(ex.getMessage());
  }
  
  @ExceptionHandler(PlayerDataRetrievalException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public Error handlePlayerDataRetrievalException(PlayerDataRetrievalException ex) {
    return new Error(ex.getMessage());
  }
  
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, String> handleValidationException(MethodArgumentNotValidException ex) {
    var errors = new HashMap<String, String>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    return errors;
  }
  
//  @ExceptionHandler(HttpMessageNotReadableException.class)
//  @ResponseStatus(HttpStatus.BAD_REQUEST)
//  public Error handleHttpMessageNotReadableException() {
//    return new Error("Le message ne peut pas etre lu - JSON parsing erreur");
//  }
}
