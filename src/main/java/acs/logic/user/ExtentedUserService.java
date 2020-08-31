package acs.logic.user;

import java.util.List;

import acs.rest.users.UserBoundary;

public interface ExtentedUserService extends UserService{
	
	public List<UserBoundary> getAllUsers(String adminDomain, String adminEmail, int page, int size);

}
