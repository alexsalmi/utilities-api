package com.asalmi.utilities_api.service.serviceImpl;

import java.io.IOException;
import java.util.List;

import com.asalmi.utilities_api.constants.Constants;
import com.asalmi.utilities_api.exception.UtilitiesApiException;
import com.asalmi.utilities_api.model.ContactRequestBody;
import com.asalmi.utilities_api.model.CustomResponse;
import com.asalmi.utilities_api.model.ErrorResponse;
import com.asalmi.utilities_api.model.SuccessResponse;
import com.asalmi.utilities_api.service.ISendGridService;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service class for operations calling the SendGrid third party API
 */
@Service
public class SendGridService implements ISendGridService {

  @Value("${sendgrid.templateid.contactrequest}")
  private String TEMPLATEID_CONTACT_REQUEST;
  @Value("${sendgrid.templateid.notificationemail}")
  private String TEMPLATEID_NOTIFICATION_EMAIL;
  @Value("${sendgrid.templateid.summaryemail}")
  private String TEMPLATEID_SUMMARY_EMAIL;

  private static final Logger log = LogManager.getLogger(SendGridService.class);

  public SendGrid sendGrid;

  @Autowired
  public SendGridService(@Value("${sendgrid.apikey}") final String API_KEY) {
    this.sendGrid = new SendGrid(API_KEY);
  }

  /**
   * Send contact request email to Alex Salmi's email
   * 
   * @param {ContactRequestBody} The request body, including the sender's email,
   *                             name, message, and phone number (optional)
   * @return Custom Response with success or failure message
   */
  public CustomResponse sendContactRequest(ContactRequestBody request) throws UtilitiesApiException {
    log.info("Sending contact request");

    // Build Mail object to send to Alex Salmi
    Email from = new Email(Constants.SENDGRID_NOREPLY_EMAIL, Constants.SENDGRID_NOREPLY_NAME);
    String templateId = TEMPLATEID_CONTACT_REQUEST;
    Email to = new Email(Constants.SENDGRID_ASALMI_EMAIL);
    Email replyTo = new Email(request.getEmail(), request.getName());

    // Add the dynamic data to the email template
    Personalization personalization = new Personalization();
    personalization.addTo(to);
    personalization.addDynamicTemplateData(Constants.SENDGRID_PERSONALIZATION_NAME, request.getName());
    personalization.addDynamicTemplateData(Constants.SENDGRID_PERSONALIZATION_EMAIL, request.getEmail());
    personalization.addDynamicTemplateData(Constants.SENDGRID_PERSONALIZATION_MESSAGE, request.getMessage());
    if (request.getPhone() == null) {
      personalization.addDynamicTemplateData(Constants.SENDGRID_PERSONALIZATION_PHONE, "N/A");
    } else {
      personalization.addDynamicTemplateData(Constants.SENDGRID_PERSONALIZATION_PHONE, request.getPhone());
    }

    Mail mail = new Mail();
    mail.setFrom(from);
    mail.setTemplateId(templateId);
    mail.addPersonalization(personalization);
    mail.replyTo = replyTo;

    // Send the email using the SendGrid API
    return sendEmail(mail);
  }

  /**
   * Send email to user's email notifying them that Alex got their message
   * 
   * @param {ContactRequestBody} The request body, including the sender's email,
   *                             name, message, and phone number (optional)
   * @return Custom Response with success or failure message
   */
  public CustomResponse sendNotificationEmail(ContactRequestBody request) throws UtilitiesApiException {
    log.info("Sending notification email");

    // Build Mail object to send the notification email to the user
    Email from = new Email(Constants.SENDGRID_NOREPLY_EMAIL, Constants.SENDGRID_ASALMI_NAME);
    Email to = new Email(request.getEmail(), request.getName());
    String templateId = TEMPLATEID_NOTIFICATION_EMAIL;

    // Add the dynamic data to the email template
    Personalization personalization = new Personalization();
    personalization.addTo(to);
    personalization.addDynamicTemplateData(Constants.SENDGRID_PERSONALIZATION_NAME, request.getName());
    personalization.addDynamicTemplateData(Constants.SENDGRID_PERSONALIZATION_MESSAGE, request.getMessage());

    Mail mail = new Mail();
    mail.setFrom(from);
    mail.setTemplateId(templateId);
    mail.addPersonalization(personalization);

    // Send the email using the SendGrid API
    return sendEmail(mail);
  }

  /**
   * Send email to Alex Salmi's email with a weekly summary of emails being sent
   * in the past month
   * 
   * @param {List<ContactRequestBody>} The contact requests received in the past
   *                                   month
   * @param {Long}                     The total number of contact requests
   *                                   received all time
   * @return Custom Response with success or failure message
   */
  public CustomResponse sendSummaryEmail(List<ContactRequestBody> monthlyRequests, long totalRequests)
      throws UtilitiesApiException {
    log.info("Sending weekly summary email");

    // Build Mail object to send the summary email to Alex Salmi
    Email from = new Email(Constants.SENDGRID_NOREPLY_EMAIL, Constants.SENDGRID_NOREPLY_NAME);
    Email to = new Email(Constants.SENDGRID_ASALMI_EMAIL);
    String templateId = TEMPLATEID_SUMMARY_EMAIL;

    // Add the dynamic data to the email template
    Personalization personalization = new Personalization();
    personalization.addTo(to);
    personalization.addDynamicTemplateData(Constants.SENDGRID_PERSONALIZATION_REQUESTS, monthlyRequests);
    personalization.addDynamicTemplateData(Constants.SENDGRID_PERSONALIZATION_MONTHLY_NUMBER, monthlyRequests.size());
    personalization.addDynamicTemplateData(Constants.SENDGRID_PERSONALIZATION_TOTAL_NUMBER, totalRequests);

    Mail mail = new Mail();
    mail.setFrom(from);
    mail.setTemplateId(templateId);
    mail.addPersonalization(personalization);

    // Send the email using the SendGrid API
    return sendEmail(mail);
  }

  /**
   * Send an email using the SendGrid API
   * 
   * @param {Mail} SendGrid Mail object containing the information needed to send
   *               the email
   * @return Custom Response with success or failure message
   */
  private CustomResponse sendEmail(Mail mail) throws UtilitiesApiException {
    // Send email with SendGrid API
    Request sgRequest = new Request();
    sgRequest.setMethod(Method.POST);
    sgRequest.setEndpoint(Constants.SENDGRID_ENDPOINT);

    Response response = new Response();
    try {
      sgRequest.setBody(mail.build());
      response = sendGrid.api(sgRequest);
    } catch (IOException ex) {
      log.error("Error sending email with SendGrid API: " + ex.getMessage());
      throw new UtilitiesApiException(Constants.ERROR_CODE_SENDGRID_SEND_ERROR, ex.getMessage());
    }

    log.info("SendGrid reponse: " + response.getStatusCode());

    // If SendGrid returns a failed statuscode, return error response
    if (response.getStatusCode() != Constants.SENDGRID_SEND_SUCCESS_CODE) {
      log.error("Email failed to send using SendGrid API");
      log.error("SendGrid response: " + response.getBody());
      return new ErrorResponse(Constants.ERROR_CODE_SENDGRID_SEND_FAILED, response.getBody());
    }

    log.info("Email sent successfully!");

    // If no errors occurred, return success repsonse
    return new SuccessResponse();
  }
}
