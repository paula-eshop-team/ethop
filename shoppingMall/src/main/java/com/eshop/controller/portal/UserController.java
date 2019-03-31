package com.eshop.controller.portal;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alipay.api.internal.util.StringUtils;
import com.eshop.common.Const;
import com.eshop.common.ResponseCode;
import com.eshop.common.ServerResponse;
import com.eshop.dao.UserMapper;
import com.eshop.pojo.User;
import com.eshop.service.IUserService;
import com.eshop.utilities.CookieUtil;
import com.eshop.utilities.JsonUtil;
import com.eshop.utilities.RedisPoolUtil;

/**
 * Description: 
 * This is the controller to handle the request for user access permission and user information management:
 *    1. User login
 *    2. User logout
 *    3. Reset user password when user logins
 *    4. Reset user password when user forgets original password with below processes:
 *    		a.	Get secrete question to help on following password reset.
 *    		b.	Validate the answer of secrete question from client’s request. If answers are correct, then provide a token for client .
 *    		c.	Reset password with username, new password and token.
 *    5. Get User Information
 *    6. Validate user information according to type, like username and email
 *    7. Update User Information
 *    
 * @author Paula Lin
 *
 */
@Controller
@RequestMapping("/user")
public class UserController {
	
	private static Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private IUserService iUserService;
	
	/**
	 * 
	 * Description:
	 * This method handler is provided to receive new user's registration.
	 * 
	 * @param user
	 * @return
	 */
	@RequestMapping(value="register.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> reigster(User user) {
		return iUserService.register(user);
	}
	
	/**
	 * 
	 * Description:
	 * This method handler is provided for user access and related validation.
	 * 
	 * @param username
	 * @param password
	 * @param session
	 * @param httpServletResponse
	 * @return
	 * 
	 */
	@RequestMapping(value="login.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> login(String username, String password, HttpSession session, HttpServletResponse httpServletResponse) {
		
		//Prepare server response
		ServerResponse<User> response = iUserService.login(username, password);
		
		//if pass the validation for user access, then cache user's info <sessionId, UserInfo> as loginToken to redis for 30 min
		//RedisCacheExtime.REDIS_SESSION_EXTIME = 30 min
		if(response.isSuccess()) {
			CookieUtil.writeLoginToken(httpServletResponse, session.getId());
			RedisPoolUtil.setEx(session.getId(), Const.RedisCacheExtime.REDIS_SESSION_EXTIME, JsonUtil.obj2String(response.getData()));
		}
		return response;
	}
	
	/**
	 * 
	 * Description:
	 * This method handler is provided for user logout.
	 * 
	 * @param session
	 * @param httpServletRequest
	 * @param httpServletResponse
	 * @return
	 * 
	 */
	@RequestMapping(value="logout.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> logout(HttpSession session, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		//Retrieve the loginToken from request
		String loginToken = CookieUtil.readLoginToken(httpServletRequest);
		//Delete loginToken in response
		CookieUtil.delLoginToken(httpServletRequest, httpServletResponse);
		//Delete loginToken in Redis.
		RedisPoolUtil.del(loginToken);
		return ServerResponse.createBySuccess();
	}
	
	
	/**
	 * 
	 * Description:
	 * This method handler is provided for user logout.
	 * 
	 * @param value
	 * @param type
	 * @return
	 * 
	 */
	@RequestMapping(value="check_valid.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> checkValid(String value, String type){
		
		//validate user information according to type, like username, email..etc
		return iUserService.checkValid(value, type);
	}
		
	/**
	 * 
	 * Description:
	 * This method handler is provided to get secrete question for further password resetting when user forget original password.
	 * 
	 * @param username
	 * @return
	 * 
	 */
	@RequestMapping(value="forget_pwd_get_secretQuestion.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> GetSecretQuestion(String username) {
		//Retrieve questions according to username
		return iUserService.selectSecretQuestion(username);
	}
	
	/**
	 * 
	 * Description:
	 * This method handler is provided to check user's answers for secret questions when forget original password.
	 * 
	 * @param username
	 * @param question
	 * @param answer
	 * @return
	 * 
	 */
	@RequestMapping(value="forget_pwd_check_answer.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> CheckAnswer(String username, String question, String answer) {
		//Return token for password resetting when answer of secrete questions are correct
		return iUserService.checkAnswer(username, question, answer);
	}
	
	/**
	 * 
	 * Description:
	 * This method handler is provided to reset user's password when forget original password
	 * 
	 * @param username
	 * @param passwordNew
	 * @param forgetToken
	 * @return
	 * 
	 */
	@RequestMapping(value="forget_reset_password.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String token) {
		//Update user's new password according to username and token
		return iUserService.forgetResetPassword(username, passwordNew, token);
	}
	
	/**
	 * Description:
	 * This method handler is provided to reset user's password after user login and with old password
	 * 
	 * @param httpServletRequest
	 * @param passwordOld
	 * @param passwordNew
	 * @return
	 * 
	 */
	@RequestMapping(value="reset_password.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> resetPassword(HttpServletRequest httpServletRequest, String passwordOld, String passwordNew) {
		
		String loginToken = CookieUtil.readLoginToken(httpServletRequest);
		//Check if loginToken is empty
		if (StringUtils.isEmpty(loginToken)) {
			return ServerResponse.createByErrorMessage(Const.ErrorMessage.USER_NOT_LOGIN);
		}
		
		//Retrieve json string of user information from Redis according to loginToken
		String userJsonStr = RedisPoolUtil.get(loginToken);
		//Transfer json string of user info to User object.
		User user = JsonUtil.string2Obj(userJsonStr, User.class);

		if (user == null) {
			return ServerResponse.createByErrorMessage(Const.ErrorMessage.USER_NOT_LOGIN);
		}
		
		//reset user's password according to old password and new password
		return iUserService.resetPassword(passwordOld, passwordNew, user);
	}
	
	/**
	 * Description:
	 * This method handler is provided to get user's information
	 * 
	 * @param httpServletRequest
	 * @return
	 * 
	 */
	@RequestMapping(value="get_user_info.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> getUserInfo(HttpServletRequest httpServletRequest) {
		//Retrieve loginToken from request
		String loginToken = CookieUtil.readLoginToken(httpServletRequest);
		
		//Check if loginToken is empty
		if(StringUtils.isEmpty(loginToken)) {
			logger.error("User is not logged in!, failed to get information with empty loginToken");
			return ServerResponse.createByErrorMessage("User is not logged in, failed to get information for user!");
		}
		
		//Retrieve json string of user information from Redis according to loginToken
		String userJsonStr = RedisPoolUtil.get(loginToken);
		
		//Transfer json string of user info to User object.
		User user = JsonUtil.string2Obj(userJsonStr, User.class);
		
		if(null == user) {
			logger.error(" User is not logged in, failed to get information for user!");
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), Const.ErrorMessage.USER_NOT_LOGIN);
		}
		
		return iUserService.getInfomation(user.getId());
	}
	
	/**
	 * 
	 * Description:
	 * This method handler is provided to update user's information
	 * 
	 * @param httpServletRequest
	 * @param user
	 * @return
	 * 
	 */
	@RequestMapping(value="update_infomation.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> update_infomation(HttpServletRequest httpServletRequest, User user) {
		//Retrieve loginToken from request
		String loginToken = CookieUtil.readLoginToken(httpServletRequest);
		
		//Check if loginToken is empty
		if(StringUtils.isEmpty(loginToken)) {
			return ServerResponse.createByErrorMessage(Const.ErrorMessage.USER_NOT_LOGIN);
		}
		
		//Retrieve json string of user information from Redis according to loginToken
		String userJsonStr = RedisPoolUtil.get(loginToken);
		//Transfer json string of user info to User object.
		User currentUser = JsonUtil.string2Obj(userJsonStr, User.class);		
		
		if(currentUser == null) {
			return ServerResponse.createByErrorMessage(Const.ErrorMessage.USER_NOT_LOGIN);
		}
		
		//Update user's information
		currentUser.setId(currentUser.getId());
		currentUser.setUsername(currentUser.getUsername());
		ServerResponse<User> response = iUserService.updateInfomation(currentUser);
		
		//After succeed to update user's information , reset the expired time of loginToken
		if(response.isSuccess()) {
			RedisPoolUtil.setEx(loginToken, Const.RedisCacheExtime.REDIS_SESSION_EXTIME, JsonUtil.obj2String(response.getData()));
		}
		return response;
	}

}
