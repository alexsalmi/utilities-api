package com.asalmi.utilities_api.exception;

import com.asalmi.utilities_api.model.ErrorResponse;

/**
 * Custom Exception class for the Utilities API
 */
public class UtilitiesApiException extends Exception {

  private ErrorResponse errorResponse;

  public UtilitiesApiException(Integer code) {
    this.errorResponse = new ErrorResponse(code);
  }

  public UtilitiesApiException(Integer code, Object details) {
    this.errorResponse = new ErrorResponse(code);
    this.errorResponse.setDetails(details);
  }

  public ErrorResponse getErrorResponse() {
    return this.errorResponse;
  }
}
