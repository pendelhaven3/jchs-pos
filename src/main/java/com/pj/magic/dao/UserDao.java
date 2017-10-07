package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.User;

public interface UserDao {

	void save(User user);
	
	User get(long id);
	
	User findByUsername(String username);

	List<User> getAll();

	void updatePassword(User user, String password);
	
}
