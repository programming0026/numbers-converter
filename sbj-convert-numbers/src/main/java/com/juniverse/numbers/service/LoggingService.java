package com.juniverse.numbers.service;

import com.juniverse.numbers.dto.RequestResponseDataDTO;

/**
 * Servicio para registrar los datos de la petición y la respuesta.
 *
 * @author ajuar
 */
public interface LoggingService {

  /**
   * Imprime el registro de la petición y la respuesta.
   *
   * @param data datos de respuesta de petición
   */
  void printLogReqRes(RequestResponseDataDTO data);
 
}
