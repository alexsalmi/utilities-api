package com.asalmi.utilities_api.model;

import com.asalmi.utilities_api.constants.Constants;

/**
 * Success Response class to be returned as REST response for success scenarios
 */
public class SuccessResponse extends CustomResponse {

  public SuccessResponse() {
    this.setCode(Constants.SUCCESS_CODE);
    this.setMessage(Constants.SUCCESS_MESSAGE);
    this.setDetails("");
  }

  public SuccessResponse(String details) {
    this.setCode(Constants.SUCCESS_CODE);
    this.setMessage(Constants.SUCCESS_MESSAGE);
    this.setDetails(details);
  }

  public SuccessResponse(Object details) {
    this.setCode(Constants.SUCCESS_CODE);
    this.setMessage(Constants.SUCCESS_MESSAGE);
    this.setDetails(details);
  }

  public boolean hasError() {
    return false;
  }
}