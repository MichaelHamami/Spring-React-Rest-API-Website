package acs.data.users;


import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import acs.data.utils.UserIdEntity;
import acs.data.utils.UserRole;




@Entity
@Table(name = "USERS")
public class UserEntity {
	
	private UserIdEntity userId;
	private UserRole role;
	private String username;
	private String avatar;

	public UserEntity() {

	}

	public UserEntity(UserIdEntity userId,  String username, UserRole role, String avatar) {
		super();
		this.userId = userId;
		this.username = username;
		this.role = role;
		this.avatar = avatar;
	}

	
	@EmbeddedId
	public UserIdEntity getUserId() {
		return userId;
	}

	public void setUserId(UserIdEntity userId) {
		this.userId = userId;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	//@Embedded
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Enumerated(EnumType.STRING)
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
