package com.asalmi.utilities_api.service;

import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.asalmi.utilities_api.constants.Constants;
import com.asalmi.utilities_api.exception.UtilitiesApiException;
import com.asalmi.utilities_api.model.ContactRequest;
import com.asalmi.utilities_api.model.ContactRequestBody;
import com.asalmi.utilities_api.model.CustomResponse;
import com.asalmi.utilities_api.model.ErrorResponse;
import com.asalmi.utilities_api.model.SuccessResponse;
import com.asalmi.utilities_api.repository.IContactRepository;
import com.asalmi.utilities_api.service.serviceImpl.ContactService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ContactServiceTest {

  @Mock
  private ISendGridService sendGridService;

  @Mock
  private IContactRepository contactRepository;

  @InjectMocks
  private ContactService contactService;

  private ContactRequestBody contactRequestBody;
  private ContactRequest contactRequest;
  private SuccessResponse successResponse;
  private List<ContactRequest> contactRequests;

  @Before
  public void Setup() {
    contactRequestBody = ContactRequestBody.builder()
        .email("test@test.com")
        .name("Test Name")
        .message("Test Message")
        .build();

    contactRequest = ContactRequest.builder()
        .id(Long.parseLong("1"))
        .email("test@test.com")
        .name("Test Name")
        .message("Test Message")
        .createdAt(LocalDateTime.now())
        .build();

    successResponse = new SuccessResponse();

    contactRequests = new ArrayList<ContactRequest>();
    contactRequests.add(contactRequest);
  }

  @Test
  public void send_Success_NoPhone() throws Exception {
    Mockito.when(sendGridService.sendContactRequest(any(ContactRequestBody.class))).thenReturn(successResponse);
    Mockito.when(sendGridService.sendNotificationEmail(any(ContactRequestBody.class))).thenReturn(successResponse);
    Mockito.when(contactRepository.save(any(ContactRequest.class))).thenReturn(contactRequest);

    SuccessResponse expectedResponse = new SuccessResponse(Constants.SUCCESS_DETAILS_SEND_CONTACT_REQUEST);

    CustomResponse actualResponse = contactService.send(contactRequestBody);
    Assert.assertEquals(expectedResponse.getCode(), actualResponse.getCode());
    Assert.assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
    Assert.assertEquals(expectedResponse.getDetails(), actualResponse.getDetails());
  }

  @Test
  public void send_Success_WithPhone() throws Exception {
    ContactRequestBody request = contactRequestBody;
    request.setPhone("1234567890");

    ContactRequest entity = contactRequest;
    request.setPhone("1234567890");

    Mockito.when(sendGridService.sendContactRequest(any(ContactRequestBody.class))).thenReturn(successResponse);
    Mockito.when(sendGridService.sendNotificationEmail(any(ContactRequestBody.class))).thenReturn(successResponse);
    Mockito.when(contactRepository.save(any(ContactRequest.class))).thenReturn(entity);

    SuccessResponse expectedResponse = new SuccessResponse(Constants.SUCCESS_DETAILS_SEND_CONTACT_REQUEST);

    CustomResponse actualResponse = contactService.send(request);
    Assert.assertEquals(expectedResponse.getCode(), actualResponse.getCode());
    Assert.assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
    Assert.assertEquals(expectedResponse.getDetails(), actualResponse.getDetails());
  }

  @Test
  public void send_ServiceError_SendGrid1() throws Exception {
    ErrorResponse errorResponse = new ErrorResponse(Constants.ERROR_CODE_SENDGRID_SEND_FAILED, "Error details");

    Mockito.when(sendGridService.sendContactRequest(any(ContactRequestBody.class))).thenReturn(errorResponse);

    CustomResponse actualResponse = contactService.send(contactRequestBody);
    Assert.assertEquals(errorResponse.getCode(), actualResponse.getCode());
    Assert.assertEquals(errorResponse.getMessage(), actualResponse.getMessage());
    Assert.assertEquals(errorResponse.getDetails(), actualResponse.getDetails());
  }

  @Test
  public void send_ServiceError_SendGrid2() throws Exception {
    ErrorResponse errorResponse = new ErrorResponse(Constants.ERROR_CODE_SENDGRID_SEND_FAILED, "Error details");

    Mockito.when(sendGridService.sendContactRequest(any(ContactRequestBody.class))).thenReturn(successResponse);
    Mockito.when(sendGridService.sendNotificationEmail(any(ContactRequestBody.class))).thenReturn(errorResponse);

    CustomResponse actualResponse = contactService.send(contactRequestBody);
    Assert.assertEquals(errorResponse.getCode(), actualResponse.getCode());
    Assert.assertEquals(errorResponse.getMessage(), actualResponse.getMessage());
    Assert.assertEquals(errorResponse.getDetails(), actualResponse.getDetails());
  }

  @Test
  public void send_RepositoryException() throws Exception {
    IllegalArgumentException exception = new IllegalArgumentException("Test exception");

    Mockito.when(sendGridService.sendContactRequest(any(ContactRequestBody.class))).thenReturn(successResponse);
    Mockito.when(sendGridService.sendNotificationEmail(any(ContactRequestBody.class))).thenReturn(successResponse);
    Mockito.when(contactRepository.save(any(ContactRequest.class))).thenThrow(exception);

    UtilitiesApiException expectedException = new UtilitiesApiException(Constants.ERROR_CODE_DATABASE_SAVE_CR,
        "Test exception");

    try {
      contactService.send(contactRequestBody);
    } catch (UtilitiesApiException actualException) {
      Assert.assertEquals(expectedException.getErrorResponse().getCode(), actualException.getErrorResponse().getCode());
      Assert.assertEquals(expectedException.getErrorResponse().getMessage(),
          actualException.getErrorResponse().getMessage());
      Assert.assertEquals(expectedException.getErrorResponse().getDetails(),
          actualException.getErrorResponse().getDetails());
    }
  }

  @Test
  public void get_Success_NoEmail() throws Exception {
    Mockito.when(contactRepository.findAll()).thenReturn(contactRequests);

    List<ContactRequest> actualResponse = contactService.get(null);
    Assert.assertEquals(actualResponse, contactRequests);
  }

  @Test
  public void get_Success_WithEmail() throws Exception {
    Mockito.when(contactRepository.findByEmail(any(String.class))).thenReturn(contactRequests);

    List<ContactRequest> actualResponse = contactService.get("test@email.com");
    Assert.assertEquals(actualResponse, contactRequests);
  }

  @Test
  public void get_RepositoryException() throws Exception {
    IllegalArgumentException exception = new IllegalArgumentException("Test exception");

    Mockito.when(contactRepository.findAll()).thenThrow(exception);

    UtilitiesApiException expectedException = new UtilitiesApiException(Constants.ERROR_CODE_DATABASE_FETCH_CR,
        "Test exception");

    try {
      contactService.get(null);
    } catch (UtilitiesApiException actualException) {
      Assert.assertEquals(expectedException.getErrorResponse().getCode(), actualException.getErrorResponse().getCode());
      Assert.assertEquals(expectedException.getErrorResponse().getMessage(),
          actualException.getErrorResponse().getMessage());
      Assert.assertEquals(expectedException.getErrorResponse().getDetails(),
          actualException.getErrorResponse().getDetails());
    }
  }

  @Test
  public void getById_Success() throws Exception {
    Mockito.when(contactRepository.findOneById(any(Long.class))).thenReturn(contactRequest);

    ContactRequest actualResponse = contactService.getById(Long.parseLong("1"));
    Assert.assertEquals(actualResponse, contactRequest);
  }

  @Test
  public void getById_RepositoryException() throws Exception {
    IllegalArgumentException exception = new IllegalArgumentException("Test exception");

    Mockito.when(contactRepository.findOneById(any(Long.class))).thenThrow(exception);

    UtilitiesApiException expectedException = new UtilitiesApiException(Constants.ERROR_CODE_DATABASE_FETCH_CR,
        "Test exception");

    try {
      contactService.getById(Long.parseLong("1"));
    } catch (UtilitiesApiException actualException) {
      Assert.assertEquals(expectedException.getErrorResponse().getCode(), actualException.getErrorResponse().getCode());
      Assert.assertEquals(expectedException.getErrorResponse().getMessage(),
          actualException.getErrorResponse().getMessage());
      Assert.assertEquals(expectedException.getErrorResponse().getDetails(),
          actualException.getErrorResponse().getDetails());
    }
  }
}
