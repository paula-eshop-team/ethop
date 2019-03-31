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
		// 密码登录MD5
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
		// 校验用户名是否存在
		ServerResponse validatedResponse = this.checkValid(user.getUsername(), Const.USERNAME);
		if (!validatedResponse.isSuccess()) {
			return validatedResponse;
		}
		// 校验email是否存在
		validatedResponse = this.checkValid(user.getUsername(), Const.EMAIL);
		if (!validatedResponse.isSuccess()) {
			return validatedResponse;
		}

		user.setRole(Const.Role.ROLE_CUSTOMER);
		// MD5 非对称加密
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
			// 开始校验
			if (Const.USERNAME.equals(type)) {
				// 校验用户名是否存在
				resultCount = userMapper.checkUsesUserName(value);
				if (resultCount > 0) {
					return ServerResponse.createByErrorMessage("用户名已存在");
				}
			}
			if (Const.EMAIL.equals(type)) {
				// 校验email是否存在
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
			// 用户不存在
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
		// 使用本地的Guava缓存来做token
		int resultCount = userMapper.checkSaftyAnswer(username, question, answer);
		if (resultCount > 0) {
			// 说明问题及问题答案是这个用户的,并且是正确的
			String forgetToken = UUID.randomUUID().toString();
			
			//迁移GuavaCache到Redis, 缓存有效期是12小时
			RedisPoolUtil.setEx(Const.TOKEN_PREFIX + username, 60*60*12, forgetToken);
			logger.info("tokenName: " + Const.TOKEN_PREFIX + username + ", forgetToken: " + forgetToken);
			//二期修改-end
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
		// 校验用户名是否存在
		ServerResponse validatedResponse = this.checkValid(username, Const.USERNAME);
		if (validatedResponse.isSuccess()) {
			//用户不存在->在checkValid是校验成功
			return validatedResponse;
		}
		//二期修改-start
		//迁移GuavaCache到Redis
		String token = RedisPoolUtil.get(Const.TOKEN_PREFIX + username);
		//二期修改-end
		
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
		//为防止横向越线,要校验一下这个用户的旧密码, 一定要指定是这个用户,因为我们会查询一个count(1),如果不指定id,那么结果就是ture或count>0
		System.out.println(MD5Util.MD5EncodeUtf8(passwordOld));
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
		//username不能被更新
		//email也要进行一个校验,校验新的email是不是已经存在,并且存在的email如果相同的话,不能是我们当前的这个用户的.
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
		
		//只更新Id,email,phone,question,answer, updateTime
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
