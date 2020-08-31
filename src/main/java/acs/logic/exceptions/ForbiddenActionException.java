package acs.logic.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class ForbiddenActionException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5849266470632923557L;

	public ForbiddenActionException() {
		super();
	}

	public ForbiddenActionException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public ForbiddenActionException(String message) {
		super(message);
		
	}

	public ForbiddenActionException(Throwable cause) {
		super(cause);
	}
}
