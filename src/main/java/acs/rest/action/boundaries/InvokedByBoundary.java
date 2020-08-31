package acs.rest.action.boundaries;

import acs.rest.utils.UserIdBoundary;

public class InvokedByBoundary {
	private UserIdBoundary userId;
	
	public InvokedByBoundary() {
	}
	
	public InvokedByBoundary(UserIdBoundary userId) {
		super();
		this.userId = userId;
	}
	
	public UserIdBoundary getUserId() {
		return userId;
	}

	public void setUserId(UserIdBoundary userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "InvokedByBoundary [userId=" + userId + "]";
	}


}
