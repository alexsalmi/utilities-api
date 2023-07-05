package com.asalmi.utilities_api.service;

import com.asalmi.utilities_api.exception.UtilitiesApiException;

/**
 * Interface for Scheduler Service class
 */
public interface ISchedulerService {
  // Send the weekly summary email to ALex Salmi's email every week
  void weeklySummaryEmail() throws UtilitiesApiException;
}
