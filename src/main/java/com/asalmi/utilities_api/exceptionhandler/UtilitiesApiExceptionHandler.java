package com.asalmi.utilities_api.exceptionhandler;

import java.util.List;
import java.util.stream.Collectors;

import com.asalmi.utilities_api.constants.Constants;
import com.asalmi.utilities_api.exception.UtilitiesApiException;
import com.asalmi.utilities_api.model.CustomResponse;
import com.asalmi.utilities_api.model.ErrorResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Exception handler class to return ErrorResponses when exceptions are thrown
 */
@ControllerAdvice
public class UtilitiesApiExceptionHandler {

  private static final Logger log = LogManager.getLogger(UtilitiesApiExceptionHandler.class);

  /**
   * Handles a thrown UtilitiesApiException, and returns an ErrorResponse
   * 
   * @param {UtilitiesApiException} The exception to be handled
   * @return Error Response with the corresponding error message
   */
  @ExceptionHandler(UtilitiesApiException.class)
  protected ResponseEntity<CustomResponse> handleException(UtilitiesApiException ex) {
    ErrorResponse response = ex.getErrorResponse();

    HttpStatus httpStatus = HttpStatus.valueOf(response.getHttpStatus());

    log.error("Error: " + response.getMessage());

    return ResponseEntity.status(httpStatus).body(response);
  }

  /**
   * Handles a thrown MethodArgumentNotValidException when invalid input is
   * provided, and returns an ErrorResponse
   * 
   * @param {MethodArgumentNotValidException} The exception to be handled
   * @return Error Response with the corresponding error message
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  protected ResponseEntity<CustomResponse> handleValidationErrors(MethodArgumentNotValidException ex)
      throws UtilitiesApiException {
    List<String> errors = ex.getBindingResult()
        .getAllErrors()
        .stream()
        .map(error -> error.getDefaultMessage())
        .collect(Collectors.toList());

    ErrorResponse response = new ErrorResponse(Constants.ERROR_CODE_BAD_REQUEST, errors);

    HttpStatus httpStatus = HttpStatus.valueOf(response.getHttpStatus());

    log.error("Validation error: " + errors);

    return ResponseEntity.status(httpStatus).body(response);
  }
}
