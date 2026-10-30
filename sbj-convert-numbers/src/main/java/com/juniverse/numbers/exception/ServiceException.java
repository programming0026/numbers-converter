package com.juniverse.numbers.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

/**
 * The Class ServiceException.
 * 
 *  @author ajuarez
 */
public class ServiceException extends ResponseStatusException {

	/** The reason. */
	private final String reason;

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -3461038041269238767L;

	/**
	 * Instantiates a new service exception.
	 *
	 * @param status the status
	 * @param reason the reason
	 */
	public ServiceException(HttpStatusCode status, String reason) {
		super(status, reason);
		this.reason = reason;
	}

	/**
	 * Instantiates a new service exception with cause.
	 *
	 * @param status the status
	 * @param reason the reason
	 * @param cause the cause
	 */
	public ServiceException(HttpStatusCode status, String reason, Throwable cause) {
		super(status, reason, cause);
		this.reason = reason;
	}

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	@Override
	public String getMessage() {
		return reason;
	}

}
