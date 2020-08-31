package acs.logic.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class PageNotFound extends RuntimeException{

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 5849266470632923557L;

	public PageNotFound() {
		super();
	}

	public PageNotFound(String message, Throwable cause) {
		super(message, cause);
		
	}

	public PageNotFound(String message) {
		super(message);
		
	}

	public PageNotFound(Throwable cause) {
		super(cause);
	}
}
