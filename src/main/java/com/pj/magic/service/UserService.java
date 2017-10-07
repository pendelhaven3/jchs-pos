package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.User;

public interface UserService {

	void save(User user);
	
	List<User> getAllUsers();
	
	User getUser(long id);
	
	User findUserByUsername(String username);

	void changePassword(User user, String newPassword);
	
	void resetPassword(User user);
	
}
