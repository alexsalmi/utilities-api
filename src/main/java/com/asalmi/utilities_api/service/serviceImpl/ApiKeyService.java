package com.asalmi.utilities_api.service.serviceImpl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asalmi.utilities_api.constants.Constants;
import com.asalmi.utilities_api.exception.UtilitiesApiException;
import com.asalmi.utilities_api.model.ApiKey;
import com.asalmi.utilities_api.model.ApiKeyResponse;
import com.asalmi.utilities_api.model.CustomResponse;
import com.asalmi.utilities_api.model.ErrorResponse;
import com.asalmi.utilities_api.model.SuccessResponse;
import com.asalmi.utilities_api.repository.IApiKeyRepository;
import com.asalmi.utilities_api.service.IApiKeyService;
import com.asalmi.utilities_api.utils.Utils;

/**
 * Service class for API Key operations
 */
@Service
public class ApiKeyService implements IApiKeyService {
  @Autowired
  private IApiKeyRepository apiKeyRepository;

  @Autowired
  private Utils utils;

  private static final Logger log = LogManager.getLogger(ApiKeyService.class);

  /**
   * Generates a new API Key
   * 
   * @param {String} consumer name
   * @param {String} application the API Key should have access to
   * @return Custom Response with the newly generated API Key, or failure message
   */
  public CustomResponse generate(String consumer, String application) throws UtilitiesApiException {
    log.info("Generating new API Key for consumer: " + consumer);

    // Generate new API Key
    String apiKey = UUID.randomUUID().toString();

    // Create hash from API Key
    String apiKeyHash;
    try {
      apiKeyHash = utils.hashString(apiKey);
    } catch (Exception ex) {
      log.info("Failed to hash API Key: " + ex.getMessage());
      throw new UtilitiesApiException(Constants.ERROR_CODE_HASH_API_KEY, ex.getMessage());
    }

    // Build API Key entity to save to database
    ApiKey apiKeyObj = ApiKey.builder()
        .apiKeyHash(apiKeyHash)
        .application(application)
        .consumer(consumer)
        .createdAt(LocalDateTime.now())
        .build();

    // Save API Key to database
    try {
      log.info("Saving API Key to database");
      apiKeyRepository.save(apiKeyObj);
    } catch (Exception ex) {
      log.info("Failed to save API Key to database: " + ex.getMessage());
      throw new UtilitiesApiException(Constants.ERROR_CODE_DATABASE_SAVE_API_KEY, ex.getMessage());
    }

    log.info("API Key created successfully");
    // If no errors occurred, return success response with the new API Key
    ApiKeyResponse response = ApiKeyResponse.builder()
        .apiKey(apiKey)
        .build();
    return new SuccessResponse(response);
  }

  /**
   * Validates an API Key
   * 
   * @param {String} The API Key to be validate
   * @return Custom Response with success or failure message
   */
  public CustomResponse validate(String apiKey) throws UtilitiesApiException {
    log.info("Validating API Key");

    // Create hash from API Key
    String apiKeyHash;
    try {
      apiKeyHash = utils.hashString(apiKey);
    } catch (Exception ex) {
      log.info("Failed to hash API Key: " + ex.getMessage());
      throw new UtilitiesApiException(Constants.ERROR_CODE_HASH_API_KEY, ex.getMessage());
    }

    ApiKey apiKeyObj = null;

    // Fetch the API Key from the database
    try {
      log.info("Checking if API Key exists in database");
      apiKeyObj = apiKeyRepository.findOneByApiKeyHash(apiKeyHash);
    } catch (Exception ex) {
      log.info("Failed to fetch API Key from database: " + ex.getMessage());
      throw new UtilitiesApiException(Constants.ERROR_CODE_DATABASE_FETCH_API_KEY, ex.getMessage());
    }

    // If API Key was not found in the database, return an error
    if (apiKeyObj == null) {
      log.info("API Key not found");
      return new ErrorResponse(Constants.ERROR_CODE_NOT_FOUND, Constants.ERROR_MESSAGE_API_KEY_NOT_FOUND);
    }

    // If no errors occurred, return success response with the API Key's consumer
    // and target application
    log.info("API Key found");
    ApiKeyResponse response = ApiKeyResponse.builder()
        .application(apiKeyObj.getApplication())
        .consumer(apiKeyObj.getConsumer())
        .build();
    return new SuccessResponse(response);
  }
}
