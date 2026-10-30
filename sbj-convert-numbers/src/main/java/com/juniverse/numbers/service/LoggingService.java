package com.juniverse.numbers.service;

import com.juniverse.numbers.dto.RequestResponseDataDTO;

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
