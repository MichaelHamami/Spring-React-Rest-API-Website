package acs.logic.exceptions;

public class EntityNotFoundException extends RuntimeException{

	private static final long serialVersionUID = -2782243048959903671L;

	public EntityNotFoundException() {
		super();
	}

	public EntityNotFoundException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public EntityNotFoundException(String message) {
		super(message);
		
	}

	public EntityNotFoundException(Throwable cause) {
		super(cause);
	}

	
}
