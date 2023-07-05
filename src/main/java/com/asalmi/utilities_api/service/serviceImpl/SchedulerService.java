package com.asalmi.utilities_api.service.serviceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.asalmi.utilities_api.constants.Constants;
import com.asalmi.utilities_api.exception.UtilitiesApiException;
import com.asalmi.utilities_api.model.ContactRequest;
import com.asalmi.utilities_api.model.ContactRequestBody;
import com.asalmi.utilities_api.model.CustomResponse;
import com.asalmi.utilities_api.repository.IContactRepository;
import com.asalmi.utilities_api.service.ISchedulerService;
import com.asalmi.utilities_api.service.ISendGridService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Service class for operations to be executed on a set schedule
 */
@Service
public class SchedulerService implements ISchedulerService {
  @Autowired
  private IContactRepository contactRepository;

  @Autowired
  private ISendGridService sendGridService;

  private static final Logger log = LogManager.getLogger(SchedulerService.class);

  /**
   * Scheduled method to send a summary email to Alex Salmi every week,
   * summarizing the contact requests received in the past month
   */
  @Scheduled(cron = "${schedule.weekly.summaryemail}")
  public void weeklySummaryEmail() throws UtilitiesApiException {
    log.info("--- STARTING SCHEDULED JOB: weeklySummaryEmail() ---");
    try {
      // Get today's date
      LocalDateTime nowDate = LocalDateTime.now();

      // Get date a month ago
      LocalDateTime monthAgoDate = nowDate.minusMonths(1);

      // Get contact requests in the past month
      List<ContactRequest> temp_monthlyRequests = contactRepository.findByCreatedAtBetween(monthAgoDate, nowDate);
      List<ContactRequestBody> monthlyRequests = new ArrayList<ContactRequestBody>();
      for (ContactRequest request : temp_monthlyRequests) {
        ContactRequestBody converted_request = ContactRequestBody.builder()
            .email(request.getEmail())
            .name(request.getName())
            .message(request.getMessage())
            .build();

        monthlyRequests.add(converted_request);
      }

      // Get total number of contact requests
      long totalRequests = contactRepository.count();

      log.info("Monthly requests and total request count fetched from Database");

      // Send contact request email to Alex Salmi's email
      CustomResponse sgResponse = sendGridService.sendSummaryEmail(monthlyRequests, totalRequests);

      // If there was an error sending the email, throw an exception
      if (sgResponse.hasError()) {
        throw new UtilitiesApiException(Constants.ERROR_CODE_SENDGRID_SEND_FAILED,
            sgResponse.getDetails());
      }

      log.info("Scheduled job completed successfully!");
    } catch (Exception ex) {
      log.info("Failed to send weekly summary email: " + ex.getMessage());
    }
  }
}
