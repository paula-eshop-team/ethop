package com.eshop.service;

import java.util.List;

import com.eshop.common.ServerResponse;
import com.eshop.pojo.User;

/**
 * 
 * @author Paula Lin
 *
 */
public interface IUserService {
	/**
	 * @param username
	 * @param password
	 * @return
	 */
	public ServerResponse<User> login(String username, String password);
	
	/**
	 * @param user
	 * @return
	 */
	public ServerResponse<String> register(User user);
	
	/**
	 * @param str
	 * @param type
	 * @return
	 */
	public ServerResponse<String> checkValid(String str, String type);
	
	/**
	 * @param username
	 * @return
	 */
	public ServerResponse selectSecretQuestion(String username);
	
	/**
	 * @param username
	 * @param question
	 * @param answer
	 * @return
	 */
	public ServerResponse<String> checkAnswer(String username, String question, String answer);
	
	/**
	 * @param username
	 * @param passwordNew
	 * @param forgetToken
	 * @return
	 */
	public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken);
	
	/**
	 * @param passwordOld
	 * @param passwordNew
	 * @param user
	 * @return
	 */
	public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user);
	
	/**
	 * @param user
	 * @return
	 */
	public ServerResponse<User> updateInfomation(User user); 
	
	/**
	 * @param userId
	 * @return
	 */
	public ServerResponse<User> getInfomation(Integer userId);
	
	/**
	 * @param user
	 * @return
	 */
	public ServerResponse checkAdminRole(User user);
	
	/**
	 * @return
	 */
	public List<User> findUserListInfo();
}
