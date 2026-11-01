package com.juniverse.numbers.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author ajuarez
 */
public class ServiceException extends ResponseStatusException {

	/** Motivo. */
	private final String reason;

	private static final long serialVersionUID = -3461038041269238767L;

	public ServiceException(HttpStatusCode status, String reason) {
		super(status, reason);
		this.reason = reason;
	}

	public ServiceException(HttpStatusCode status, String reason, Throwable cause) {
		super(status, reason, cause);
		this.reason = reason;
	}

	@Override
	public String getMessage() {
		return reason;
	}

}
