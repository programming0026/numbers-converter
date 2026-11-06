package com.juniverse.numbers.exception;

import com.juniverse.numbers.dto.ErrorResponseDTO;
import com.juniverse.numbers.util.DateUtil;
import io.swagger.v3.oas.annotations.Hidden;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

/**
 * @author Java Universe
 */
@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Procesa excepción de validación.
     *
     * @param ex excepción
     * @param request petición
     * @return entidad de respuesta
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {

        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("Error de validación");

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                message,
                "VALIDATION_ERROR",
                DateUtil.getFormat(LocalDateTime.now()),
                request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Procesa excepción de servicio.
     *
     * @param exception excepción
     * @param request petición
     * @return entidad de respuesta
     */
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponseDTO> handleServiceException(ServiceException exception, 
            WebRequest request) {
        ErrorResponseDTO response = new ErrorResponseDTO(
                exception.getMessage(),
                "ERROR",
                DateUtil.getFormat(LocalDateTime.now()),
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(response, exception.getStatusCode());
    }

    /**
     * Procesa el error de cuerpo HTTP ilegible.
     *
     * @param ex excepción
     * @param request petición
     * @return entidad de respuesta
     */
    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleHttpMessageNotReadable(
            org.springframework.http.converter.HttpMessageNotReadableException ex,
            WebRequest request) {
        ErrorResponseDTO response = new ErrorResponseDTO(
                "El formato de la petición o del número es inválido",
                "INVALID_INPUT_FORMAT",
                DateUtil.getFormat(LocalDateTime.now()),
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Procesa cualquier excepción inesperada.
     *
     * @param ex excepción
     * @param request petición
     * @return entidad de respuesta
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneralException(
            Exception ex, WebRequest request) {
        ErrorResponseDTO response = new ErrorResponseDTO(
                "Ha ocurrido un error inesperado en el servidor",
                "INTERNAL_SERVER_ERROR",
                DateUtil.getFormat(LocalDateTime.now()),
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
