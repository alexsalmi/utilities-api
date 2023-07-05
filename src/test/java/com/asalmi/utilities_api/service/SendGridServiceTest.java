package com.asalmi.utilities_api.service;

import static org.mockito.ArgumentMatchers.any;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.asalmi.utilities_api.constants.Constants;
import com.asalmi.utilities_api.exception.UtilitiesApiException;
import com.asalmi.utilities_api.model.ContactRequestBody;
import com.asalmi.utilities_api.model.CustomResponse;
import com.asalmi.utilities_api.model.ErrorResponse;
import com.asalmi.utilities_api.model.SuccessResponse;
import com.asalmi.utilities_api.service.serviceImpl.SendGridService;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;

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
public class SendGridServiceTest {
  @Mock
  private SendGrid sendGrid;

  @InjectMocks
  private SendGridService sendGridService;

  private ContactRequestBody contactRequestBody;
  private SuccessResponse successResponse;
  private Response sgResponse;

  @Before
  public void Setup() throws Exception {
    contactRequestBody = ContactRequestBody.builder()
        .email("test@test.com")
        .name("Test Name")
        .message("Test Message")
        .build();

    successResponse = new SuccessResponse();

    sgResponse = new Response(Constants.SENDGRID_SEND_SUCCESS_CODE, "Success", new HashMap<>());

    sendGridService.sendGrid = this.sendGrid;
  }

  @Test
  public void sendContactRequest_Success_NoPhone() throws Exception {
    Mockito.when(sendGrid.api(any(Request.class))).thenReturn(sgResponse);

    CustomResponse actualResponse = sendGridService.sendContactRequest(contactRequestBody);
    Assert.assertEquals(successResponse.getCode(), actualResponse.getCode());
    Assert.assertEquals(successResponse.getMessage(), actualResponse.getMessage());
    Assert.assertEquals(successResponse.getDetails(), actualResponse.getDetails());
  }

  @Test
  public void sendContactRequest_Success_WithPhone() throws Exception {
    ContactRequestBody request = contactRequestBody;
    request.setPhone("1234567890");

    Mockito.when(sendGrid.api(any(Request.class))).thenReturn(sgResponse);

    CustomResponse actualResponse = sendGridService.sendContactRequest(request);
    Assert.assertEquals(successResponse.getCode(), actualResponse.getCode());
    Assert.assertEquals(successResponse.getMessage(), actualResponse.getMessage());
    Assert.assertEquals(successResponse.getDetails(), actualResponse.getDetails());
  }

  @Test
  public void sendNotificationEmail_Success() throws Exception {
    Mockito.when(sendGrid.api(any(Request.class))).thenReturn(sgResponse);

    CustomResponse actualResponse = sendGridService.sendNotificationEmail(contactRequestBody);
    Assert.assertEquals(successResponse.getCode(), actualResponse.getCode());
    Assert.assertEquals(successResponse.getMessage(), actualResponse.getMessage());
    Assert.assertEquals(successResponse.getDetails(), actualResponse.getDetails());
  }

  @Test
  public void sendSummaryEmail_Success() throws Exception {
    Mockito.when(sendGrid.api(any(Request.class))).thenReturn(sgResponse);
    List<ContactRequestBody> monthlyRequests = new ArrayList<ContactRequestBody>();

    CustomResponse actualResponse = sendGridService.sendSummaryEmail(monthlyRequests, 2);
    Assert.assertEquals(successResponse.getCode(), actualResponse.getCode());
    Assert.assertEquals(successResponse.getMessage(), actualResponse.getMessage());
    Assert.assertEquals(successResponse.getDetails(), actualResponse.getDetails());
  }

  @Test
  public void sendEmail_Failed() throws Exception {
    Response failedSGResponse = new Response(400, "Test error", new HashMap<>());

    Mockito.when(sendGrid.api(any(Request.class))).thenReturn(failedSGResponse);

    ErrorResponse expectedResponse = new ErrorResponse(Constants.ERROR_CODE_SENDGRID_SEND_FAILED, "Test error");

    CustomResponse actualResponse = sendGridService.sendContactRequest(contactRequestBody);
    Assert.assertEquals(expectedResponse.getCode(), actualResponse.getCode());
    Assert.assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
    Assert.assertEquals(expectedResponse.getDetails(), actualResponse.getDetails());
  }

  @Test
  public void sendEmail_SendException() throws Exception {
    IOException exception = new IOException("Test exception");

    Mockito.when(sendGrid.api(any(Request.class))).thenThrow(exception);

    UtilitiesApiException expectedException = new UtilitiesApiException(Constants.ERROR_CODE_SENDGRID_SEND_ERROR,
        "Test exception");

    try {
      sendGridService.sendContactRequest(contactRequestBody);
    } catch (UtilitiesApiException actualException) {
      Assert.assertEquals(expectedException.getErrorResponse().getCode(), actualException.getErrorResponse().getCode());
      Assert.assertEquals(expectedException.getErrorResponse().getMessage(),
          actualException.getErrorResponse().getMessage());
      Assert.assertEquals(expectedException.getErrorResponse().getDetails(),
          actualException.getErrorResponse().getDetails());
    }
  }
}
