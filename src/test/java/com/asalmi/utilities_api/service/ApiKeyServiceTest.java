package com.asalmi.utilities_api.service;

import static org.mockito.ArgumentMatchers.any;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.asalmi.utilities_api.constants.Constants;
import com.asalmi.utilities_api.exception.UtilitiesApiException;
import com.asalmi.utilities_api.model.ApiKey;
import com.asalmi.utilities_api.model.ApiKeyResponse;
import com.asalmi.utilities_api.model.CustomResponse;
import com.asalmi.utilities_api.model.ErrorResponse;
import com.asalmi.utilities_api.model.SuccessResponse;
import com.asalmi.utilities_api.repository.IApiKeyRepository;
import com.asalmi.utilities_api.service.serviceImpl.ApiKeyService;
import com.asalmi.utilities_api.utils.Utils;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ApiKeyServiceTest {

  @Mock
  private IApiKeyRepository apiKeyRepository;

  @Mock
  private Utils utils;

  @InjectMocks
  private ApiKeyService apiKeyService;

  private String apiKey;
  private String consumer;
  private String application;
  private String hashedApiKey;
  private ApiKey apiKeyObj;
  private ApiKeyResponse apiKeyResponse;

  @Before
  public void Setup() {
    apiKey = "API Key";
    consumer = "Tests";
    application = "Utilities API";
    hashedApiKey = "Hashed API Key";
    apiKeyObj = ApiKey.builder()
        .apiKeyHash(hashedApiKey)
        .application(application)
        .consumer(consumer)
        .createdAt(LocalDateTime.now())
        .build();

    apiKeyResponse = ApiKeyResponse.builder()
        .application(application)
        .consumer(consumer)
        .build();
  }

  @Test
  public void generate_Success() throws Exception {
    Mockito.when(utils.hashString(any(String.class))).thenReturn(hashedApiKey);
    Mockito.when(apiKeyRepository.save(any(ApiKey.class))).thenReturn(null);

    SuccessResponse expectedResponse = new SuccessResponse(
        ApiKeyResponse.builder()
            .apiKey(apiKey)
            .build());

    CustomResponse actualResponse = apiKeyService.generate(consumer, application);
    Assert.assertEquals(expectedResponse.getCode(), actualResponse.getCode());
    Assert.assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
  }

  @Test
  public void generate_HashString_Exception() throws Exception {
    NoSuchAlgorithmException exception = new NoSuchAlgorithmException("Test exception");

    Mockito.when(utils.hashString(any(String.class))).thenThrow(exception);

    UtilitiesApiException expectedException = new UtilitiesApiException(Constants.ERROR_CODE_HASH_API_KEY,
        "Test exception");

    try {
      apiKeyService.generate(consumer, application);
    } catch (UtilitiesApiException actualException) {
      Assert.assertEquals(expectedException.getErrorResponse().getCode(), actualException.getErrorResponse().getCode());
      Assert.assertEquals(expectedException.getErrorResponse().getMessage(),
          actualException.getErrorResponse().getMessage());
      Assert.assertEquals(expectedException.getErrorResponse().getDetails(),
          actualException.getErrorResponse().getDetails());
    }
  }

  @Test
  public void generate_Repository_Exception() throws Exception {
    IllegalArgumentException exception = new IllegalArgumentException("Test exception");

    Mockito.when(utils.hashString(any(String.class))).thenReturn(hashedApiKey);
    Mockito.when(apiKeyRepository.save(any(ApiKey.class))).thenThrow(exception);

    UtilitiesApiException expectedException = new UtilitiesApiException(Constants.ERROR_CODE_DATABASE_SAVE_API_KEY,
        "Test exception");

    try {
      apiKeyService.generate(consumer, application);
    } catch (UtilitiesApiException actualException) {
      Assert.assertEquals(expectedException.getErrorResponse().getCode(), actualException.getErrorResponse().getCode());
      Assert.assertEquals(expectedException.getErrorResponse().getMessage(),
          actualException.getErrorResponse().getMessage());
      Assert.assertEquals(expectedException.getErrorResponse().getDetails(),
          actualException.getErrorResponse().getDetails());
    }
  }

  @Test
  public void validate_Success() throws Exception {
    Mockito.when(utils.hashString(any(String.class))).thenReturn(hashedApiKey);
    Mockito.when(apiKeyRepository.findOneByApiKeyHash(any(String.class))).thenReturn(apiKeyObj);

    SuccessResponse expectedResponse = new SuccessResponse(apiKeyResponse);

    CustomResponse actualResponse = apiKeyService.validate(apiKey);
    Assert.assertEquals(expectedResponse.getCode(), actualResponse.getCode());
    Assert.assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
    Assert.assertEquals(apiKeyResponse.getApplication(),
        ((ApiKeyResponse) actualResponse.getDetails()).getApplication());
    Assert.assertEquals(apiKeyResponse.getConsumer(),
        ((ApiKeyResponse) actualResponse.getDetails()).getConsumer());
  }

  @Test
  public void validate_KeyNotFound() throws Exception {
    Mockito.when(utils.hashString(any(String.class))).thenReturn(hashedApiKey);
    Mockito.when(apiKeyRepository.findOneByApiKeyHash(any(String.class))).thenReturn(null);

    ErrorResponse expectedResponse = new ErrorResponse(Constants.ERROR_CODE_NOT_FOUND,
        Constants.ERROR_MESSAGE_API_KEY_NOT_FOUND);

    CustomResponse actualResponse = apiKeyService.validate(apiKey);
    Assert.assertEquals(expectedResponse.getCode(), actualResponse.getCode());
    Assert.assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
    Assert.assertEquals(expectedResponse.getDetails(), actualResponse.getDetails());
  }

  @Test
  public void validate_HashString_Exception() throws Exception {
    NoSuchAlgorithmException exception = new NoSuchAlgorithmException("Test exception");

    Mockito.when(utils.hashString(any(String.class))).thenThrow(exception);

    UtilitiesApiException expectedException = new UtilitiesApiException(Constants.ERROR_CODE_HASH_API_KEY,
        "Test exception");

    try {
      apiKeyService.validate(apiKey);
    } catch (UtilitiesApiException actualException) {
      Assert.assertEquals(expectedException.getErrorResponse().getCode(), actualException.getErrorResponse().getCode());
      Assert.assertEquals(expectedException.getErrorResponse().getMessage(),
          actualException.getErrorResponse().getMessage());
      Assert.assertEquals(expectedException.getErrorResponse().getDetails(),
          actualException.getErrorResponse().getDetails());
    }
  }

  @Test
  public void validate_Repository_Exception() throws Exception {
    IllegalArgumentException exception = new IllegalArgumentException("Test exception");

    Mockito.when(utils.hashString(any(String.class))).thenReturn(hashedApiKey);
    Mockito.when(apiKeyRepository.findOneByApiKeyHash(any(String.class))).thenThrow(exception);

    UtilitiesApiException expectedException = new UtilitiesApiException(Constants.ERROR_CODE_DATABASE_FETCH_API_KEY,
        "Test exception");

    try {
      apiKeyService.validate(apiKey);
    } catch (UtilitiesApiException actualException) {
      Assert.assertEquals(expectedException.getErrorResponse().getCode(), actualException.getErrorResponse().getCode());
      Assert.assertEquals(expectedException.getErrorResponse().getMessage(),
          actualException.getErrorResponse().getMessage());
      Assert.assertEquals(expectedException.getErrorResponse().getDetails(),
          actualException.getErrorResponse().getDetails());
    }
  }
}
