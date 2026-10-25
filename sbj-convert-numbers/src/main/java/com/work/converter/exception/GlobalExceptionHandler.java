package com.work.converter.exception;

import com.work.converter.dto.ErrorResponseDTO;
import com.work.converter.util.DateUtil;
import io.swagger.v3.oas.annotations.Hidden;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

/**
 * The Class GlobalExceptionHandler.
 *
 * @author ajuar
 */
@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle validation exception.
     *
     * @param ex the ex
     * @param request the request
     * @return the response entity
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
     * Handle service exception.
     *
     * @param exception the exception
     * @param request the request
     * @return the response entity
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
     * Handle HTTP message not readable exception.
     *
     * @param ex the ex
     * @param request the request
     * @return the response entity
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
     * Handle general unexpected exception.
     *
     * @param ex the ex
     * @param request the request
     * @return the response entity
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
