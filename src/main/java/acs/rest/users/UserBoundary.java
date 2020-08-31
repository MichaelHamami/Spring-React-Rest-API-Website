package acs.rest.users;


import acs.data.utils.UserRole;
import acs.rest.utils.UserIdBoundary;


public class UserBoundary {
	private UserIdBoundary userId;
	private UserRole role;
	private String username;
	private String avatar;

	public UserBoundary() {

	}

	public UserBoundary(UserIdBoundary userId, UserRole role, String username, String avatar) {
		super();
		setUserId(userId);
		setUserName(username);
		setRole(role);
		setAvatar(avatar);
	}

	public UserIdBoundary getUserId() {
		return userId;
	}

	public void setUserId(UserIdBoundary userId) {
		this.userId = userId;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getUsername() {
		return username;
	}

	public void setUserName(String username) {
		this.username = username;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "UserBoundry [userId=" + userId + ", avatar=" + avatar + ", userName=" + username + ", role=" + role
				+ "]";
	}



	

}
