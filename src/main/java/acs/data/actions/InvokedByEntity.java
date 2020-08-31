package acs.data.actions;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import acs.data.utils.UserIdEntity;


@Embeddable
public class InvokedByEntity {
	private UserIdEntity userId;
	
	
	public InvokedByEntity(){
		
	}
	
	public InvokedByEntity(UserIdEntity userId) {
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
		return "InvokedByEntity [userId=" + userId + "]";
	}
	

}
