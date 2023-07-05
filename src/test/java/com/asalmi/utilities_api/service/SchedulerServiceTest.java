package com.asalmi.utilities_api.service;

import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.asalmi.utilities_api.constants.Constants;
import com.asalmi.utilities_api.model.ContactRequest;
import com.asalmi.utilities_api.model.ErrorResponse;
import com.asalmi.utilities_api.model.SuccessResponse;
import com.asalmi.utilities_api.repository.IContactRepository;
import com.asalmi.utilities_api.service.serviceImpl.SchedulerService;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SchedulerServiceTest {
  @Mock
  private ISendGridService sendGridService;

  @Mock
  private IContactRepository contactRepository;

  @InjectMocks
  private SchedulerService schedulerService;

  private ContactRequest contactRequest;
  private SuccessResponse successResponse;
  private List<ContactRequest> contactRequests;
  private long totalRequests;

  @Before
  public void Setup() {
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

    totalRequests = 1;
  }

  @Test
  public void weeklySummaryEmail_Success() throws Exception {
    Mockito.when(contactRepository.findByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
        .thenReturn(contactRequests);
    Mockito.when(contactRepository.count()).thenReturn(totalRequests);
    Mockito.when(sendGridService.sendSummaryEmail(any(List.class), any(long.class))).thenReturn(successResponse);

    schedulerService.weeklySummaryEmail();
  }

  @Test
  public void weeklySummaryEmail_RepositoryException() throws Exception {
    IllegalArgumentException exception = new IllegalArgumentException("Test exception");

    Mockito.when(contactRepository.findByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
        .thenThrow(exception);

    schedulerService.weeklySummaryEmail();
  }

  @Test
  public void send_ServiceError_SendGrid2() throws Exception {
    ErrorResponse errorResponse = new ErrorResponse(Constants.ERROR_CODE_SENDGRID_SEND_FAILED, "Error details");

    Mockito.when(contactRepository.findByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
        .thenReturn(contactRequests);
    Mockito.when(contactRepository.count()).thenReturn(totalRequests);
    Mockito.when(sendGridService.sendSummaryEmail(any(List.class), any(long.class))).thenReturn(errorResponse);

    schedulerService.weeklySummaryEmail();
  }
}
