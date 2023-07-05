package com.asalmi.utilities_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.asalmi.utilities_api.exception.UtilitiesApiException;
import com.asalmi.utilities_api.model.ApiKeyRequest;
import com.asalmi.utilities_api.model.CustomResponse;
import com.asalmi.utilities_api.model.ErrorResponse;
import com.asalmi.utilities_api.service.IApiKeyService;

import jakarta.validation.Valid;

/**
 * Controller class for API Key operations
 */
@RestController
@CrossOrigin
@RequestMapping("/apikey")
public class ApiKeyController {

  @Autowired
  private IApiKeyService apiKeyService;

  /**
   * Generates a new API Key
   * 
   * @param {ApiKeyRequest} Request body including consumer name and application
   *                        the API Key should have access to
   * @return Custom Response with the newly generated API Key, or failure message
   */
  @PostMapping("/generate")
  public ResponseEntity<CustomResponse> generate(@Valid @RequestBody ApiKeyRequest request)
      throws UtilitiesApiException {

    // Call the apiKeyService to generate a new key and save it to the database
    CustomResponse result = apiKeyService.generate(request.getConsumer(), request.getApplication());

    // If an error occurred, send an error response
    if (result.hasError()) {
      ErrorResponse errorResponse = (ErrorResponse) result;
      return ResponseEntity.status(HttpStatus.valueOf(errorResponse.getHttpStatus())).body(result);
    }

    // If success, send success response
    return ResponseEntity.status(HttpStatus.OK).body(result);
  }

  /**
   * Validates an API Key
   * 
   * @param {String} The API Key to be validate
   * @return Custom Response with success or failure message
   */
  @GetMapping("/validate/{apiKey}")
  public ResponseEntity<CustomResponse> validate(@PathVariable("apiKey") String apiKey)
      throws UtilitiesApiException {

    // Call the apiKeyService to validate the key
    CustomResponse result = apiKeyService.validate(apiKey);

    // If an error occurred, send an error response
    if (result.hasError()) {
      ErrorResponse errorResponse = (ErrorResponse) result;
      return ResponseEntity.status(HttpStatus.valueOf(errorResponse.getHttpStatus())).body(result);
    }

    // If success, send success response
    return ResponseEntity.status(HttpStatus.OK).body(result);
  }
}
