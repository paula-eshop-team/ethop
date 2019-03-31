package com.eshop.service.impl;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eshop.common.Const;
import com.eshop.common.EshopConstant;
import com.eshop.common.ServerResponse;
import com.eshop.dao.UserMapper;
import com.eshop.pojo.User;
import com.eshop.service.IUserService;
import com.eshop.utilities.MD5Util;
import com.eshop.utilities.RedisPoolUtil;

/**
 * 
 * @author Paula Lin
 *
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {

	private static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	
	@Autowired
	private UserMapper userMapper;
	
	/**
     * @param username
     * @param password
	 * @return
	 * 
	 */
	@Override
	public ServerResponse<User> login(String username, String password) {
		int resultCount = userMapper.checkUsesUserName(username);
		if (resultCount == 0) {
			return ServerResponse.createByErrorMessage(EshopConstant.USER_NOT_EXIST.toString());
		}
		String md5Password = MD5Util.MD5EncodeUtf8(password);
		User user = userMapper.selectUserLoginInfo(username, md5Password);
		if (user == null) {
			return ServerResponse.createByErrorMessage(EshopConstant.PASSWORD_ERROR.toString());
		}

		user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
		return ServerResponse.createBySuccess(EshopConstant.LOGIN_SUCCESS.toString(), user);
	}

	/**
     * @param user
	 * @return
	 * 
	 */
	public ServerResponse<String> register(User user) {
		ServerResponse validatedResponse = this.checkValid(user.getUsername(), Const.USERNAME);
		if (!validatedResponse.isSuccess()) {
			return validatedResponse;
		}
		validatedResponse = this.checkValid(user.getUsername(), Const.EMAIL);
		if (!validatedResponse.isSuccess()) {
			return validatedResponse;
		}

		user.setRole(Const.Role.ROLE_CUSTOMER);
		logger.debug("setUserPassword: " + user.getPassword() + ", MD5Password: " + MD5Util.MD5EncodeUtf8(user.getPassword()));
		user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
		
		logger.debug("userEmail: " + user.getEmail());
		System.out.println("userEmail: " + user.getEmail());
		int resultCount = userMapper.insertUser(user);
		if (resultCount == 0) {
			return ServerResponse.createByErrorMessage("注册失败");
		}
		return ServerResponse.createBySuccessMessage("注册成功");
	}

	/**
     * @param value
     * @param type
	 * @return
	 * 
	 */
	public ServerResponse<String> checkValid(String value, String type) {
		if (org.apache.commons.lang3.StringUtils.isNotBlank(type)) {
			int resultCount;
			if (Const.USERNAME.equals(type)) {
				resultCount = userMapper.checkUsesUserName(value);
				if (resultCount > 0) {
					return ServerResponse.createByErrorMessage("用户名已存在");
				}
			}
			if (Const.EMAIL.equals(type)) {
				resultCount = userMapper.checkUsersInfoByEamil(value);
				if (resultCount > 0) {
					return ServerResponse.createByErrorMessage("email已存在");
				}
			}
		} else {
			return ServerResponse.createByErrorMessage("参数错误");
		}

		return ServerResponse.createBySuccessMessage("校验成功," + type + "不存在!");
	}

	/**
     * @param username
	 * @return
	 * 
	 */
	public ServerResponse selectSecretQuestion(String username) {
		ServerResponse validatedResponse = this.checkValid(username, Const.USERNAME);
		if (validatedResponse.isSuccess()) {
			return ServerResponse.createByErrorMessage("用户不存在");
		}
		String question = userMapper.selectFaftyQuestionByUsername(username);
		if (org.apache.commons.lang3.StringUtils.isNotBlank(question)) {
			return ServerResponse.createBySuccess(question);
		}
		return ServerResponse.createByErrorMessage("返回密码的问题是空的");
	}

	/**
     * @param username
     * @param question
     * @param answer
	 * @return
	 * 
	 */
	public ServerResponse<String> checkAnswer(String username, String question, String answer) {
		int resultCount = userMapper.checkSaftyAnswer(username, question, answer);
		if (resultCount > 0) {
			String forgetToken = UUID.randomUUID().toString();
			RedisPoolUtil.setEx(Const.TOKEN_PREFIX + username, 60*60*12, forgetToken);
			logger.info("tokenName: " + Const.TOKEN_PREFIX + username + ", forgetToken: " + forgetToken);
			return ServerResponse.createBySuccess(forgetToken);
		}
		return ServerResponse.createBySuccessMessage("问题的答案错误");
	}

	/**
     * @param username
     * @param passwordNew
     * @param forgetToken
	 * @return
	 * 
	 */
	public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
		if (org.apache.commons.lang3.StringUtils.isBlank(forgetToken)) {
			return ServerResponse.createByErrorMessage(EshopConstant.PARMS_ERROR_NEED_TOKEN.toString());
		}
		ServerResponse validatedResponse = this.checkValid(username, Const.USERNAME);
		if (validatedResponse.isSuccess()) {
			return validatedResponse;
		}
		String token = RedisPoolUtil.get(Const.TOKEN_PREFIX + username);
		if(org.apache.commons.lang3.StringUtils.isBlank(token)) {
			return ServerResponse.createByErrorMessage(EshopConstant.TOKEN_INVALIDATE.toString());
		}
		
		if(org.apache.commons.lang3.StringUtils.equals(forgetToken, token)) {
			String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
			int rowCount = userMapper.updateUsersPasswordByUsername(username, md5Password);
			if (rowCount>0) {
				return ServerResponse.createBySuccessMessage(EshopConstant.CHANGE_PASSWORD_SUCCESS.toString());
			}
		}else{
			return ServerResponse.createByErrorMessage(EshopConstant.TOKEN_ERROR_NEED_REGET.toString());
		}
		return ServerResponse.createByErrorMessage(EshopConstant.CHANGE_PASSWORD_FALED.toString());
	}

	/**
     * @param passwordOld
     * @param passwordNew
     * @param user
	 * @return
	 * 
	 */
	public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user) {
		int resultCount = userMapper.checkUsersPassword(MD5Util.MD5EncodeUtf8(passwordOld), user.getId());
		if(resultCount ==0) {
			return ServerResponse.createByErrorMessage(EshopConstant.PRE_PASSWORD_ERROR.toString());
		}
		user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
		int updateCount = userMapper.updateUserByPrimaryKeySelective(user);
		if(updateCount>0) {
			return ServerResponse.createBySuccessMessage(EshopConstant.UPDATE_PASSWORD_SUCCESS.toString());
		}
		return ServerResponse.createByErrorMessage(EshopConstant.UPDATE_PASSWORD_SUCCESS.toString());
	}
	
	/**
     * @param user
	 * @return
	 * 
	 */
	public ServerResponse<User> updateInfomation(User user) {
		int resultCount = userMapper.checkUsersEmailInfoByUserId(user.getEmail(), user.getId());
		if(resultCount > 0) {
			return ServerResponse.createByErrorMessage("email已存在,请更换email在尝试更新");
		}
		User updateUser = new User();
		updateUser.setId(user.getId());
		updateUser.setEmail(user.getEmail());
		updateUser.setPhone(user.getPhone());
		updateUser.setQuestion(user.getQuestion());
		updateUser.setAnswer(user.getAnswer());
		int updateCount = userMapper.updateUserByPrimaryKeySelective(updateUser);
		if(updateCount > 0) {
			return ServerResponse.createBySuccess(EshopConstant.UPDATE_USER_INFO_SUCCESS.toString(), updateUser);
		}
		return ServerResponse.createByErrorMessage(EshopConstant.UPDATE_USER_INFO_FAILED.toString());
	}
	
	/**
     * @param userId
	 * @return
	 * 
	 */
	public ServerResponse<User> getInfomation(Integer userId) {
		User user = userMapper.selectUserByPrimaryKey(userId);
		if(user == null) {
			return ServerResponse.createByErrorMessage(EshopConstant.CANNOT_FIND_CURRENT_USER.toString());
		}
		user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
		return ServerResponse.createBySuccess(user);
	}
	

    /**
     * 校验是否是管理员
     * @param user
     * @return
     */
    public ServerResponse checkAdminRole(User user){
        if(user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

	@Override
	public List<User> findUserListInfo() {
		return userMapper.findUserListInfo();
	}
}
