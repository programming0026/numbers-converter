package com.work.converter.service;

import com.work.converter.dto.RequestResponseDataDTO;

/**
 * The Interface LoggingService.
 * Service for logging request and response data.
 *
 * @author ajuar
 */
public interface LoggingService {

  /**
   * Print log request and response.
   *
   * @param data the request response data
   */
  void printLogReqRes(RequestResponseDataDTO data);
 
}
