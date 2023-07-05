package com.asalmi.utilities_api.service;

import com.asalmi.utilities_api.exception.UtilitiesApiException;
import com.asalmi.utilities_api.model.CustomResponse;

/**
 * Interface for API Key Service class
 */
public interface IApiKeyService {

  // Generate a new API Key
  CustomResponse generate(String consumer, String application) throws UtilitiesApiException;

  // Validate an API Key
  CustomResponse validate(String apiKey) throws UtilitiesApiException;
}
