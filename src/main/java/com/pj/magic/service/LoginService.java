package com.pj.magic.service;

import com.pj.magic.exception.InvalidUsernamePasswordException;
import com.pj.magic.model.User;

public interface LoginService {

	User login(String username, String password) throws InvalidUsernamePasswordException;
	
	void logout();
	
	User getLoggedInUser();

}
