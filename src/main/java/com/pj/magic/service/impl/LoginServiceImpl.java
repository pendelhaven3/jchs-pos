package com.pj.magic.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.UserDao;
import com.pj.magic.exception.InvalidUsernamePasswordException;
import com.pj.magic.model.User;
import com.pj.magic.service.LoginService;
import com.pj.magic.util.PasswordTransformer;

@Service
public class LoginServiceImpl implements LoginService {

	@Autowired private UserDao userDao;
	
	private User loggedInUser;
	
	@Override
	public User login(String username, String password) throws InvalidUsernamePasswordException {
		User user = userDao.findByUsername(username);
		if (user == null || !PasswordTransformer.transform(password).equals(user.getPassword())) {
			throw new InvalidUsernamePasswordException();
		}
		loggedInUser = user;
		return user;
	}

	@Override
	public void logout() {
		loggedInUser = null;
	}

	@Override
	public User getLoggedInUser() {
		return loggedInUser;
	}

}
