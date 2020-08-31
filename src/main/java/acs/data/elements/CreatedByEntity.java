package acs.data.elements;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

import acs.data.utils.UserIdEntity;

@Embeddable
public class CreatedByEntity {
	
	private UserIdEntity userId;
	
	public CreatedByEntity() {
		
	}

	public CreatedByEntity(UserIdEntity userId) {
		super();
		this.userId = userId;
	}
	
	@Embedded
	public UserIdEntity getUserId() {
		return userId;
	}

	public void setUserId(UserIdEntity userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "CreatedByEntity [userId=" + userId + "]";
	}
	
	
}
