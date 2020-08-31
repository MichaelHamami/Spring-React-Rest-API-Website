package acs.rest.element.boundaries;

import acs.rest.utils.UserIdBoundary;

public class CreatedByBoundary {
	
	private UserIdBoundary userId;
	
	public CreatedByBoundary() {
	}
	
	public CreatedByBoundary(UserIdBoundary userId) {
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
		return "CreatedByBoundary [userId=" + userId + "]";
	}
}