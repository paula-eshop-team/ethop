package com.eshop.controller.backend;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eshop.common.Const;
import com.eshop.common.ServerResponse;
import com.eshop.pojo.User;
import com.eshop.service.IUserService;
import com.eshop.utilities.CookieUtil;
import com.eshop.utilities.JsonUtil;
import com.eshop.utilities.RedisShardedPoolUtil;

/**
 * Description: 
 * This is the controller to handle the request for admin' access permission and user information management
 *    
 * @author Paula Lin
 *
 */
@Controller
@RequestMapping("/manage/user")
public class UserManageController {
	
	private static Logger logger = LoggerFactory.getLogger(UserManageController.class);
	@Autowired
	private IUserService iUserService;
	
	/**
	 * Description: 
	 * This method handler is provided for admin user access and related validation.
	 * 
	 * @param username
	 * @param password
	 * @param session
	 * @param httpServletResponse
	 * @return
	 */
	@RequestMapping(value="login.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> login(String username, String password, HttpSession session, HttpServletResponse httpServletResponse) {
	
		//User common login validation
		ServerResponse response = iUserService.login(username, password);
		
		if(response.isSuccess()) {
			//If pass the common login validation
			//get user data
			User user = (User) response.getData();
			
			//Check the role of user, only user with ADMIN role can login background system
			if(user.getRole() == Const.Role.ROLE_ADMIN) {
				//If user is with ADMIN role, then can successfully login and cache user's info <sessionId, UserInfo> as loginToken to redis for 30 min
                CookieUtil.writeLoginToken(httpServletResponse,session.getId());
                RedisShardedPoolUtil.setEx(session.getId(), Const.RedisCacheExtime.REDIS_SESSION_EXTIME, JsonUtil.obj2String(response.getData()));
                return response;
			}else {
				//return error message: "User is not admin, could not login backgound system!"
				return ServerResponse.createByErrorMessage(Const.ErrorMessage.USER_IS_NOT_ADMIN);
			}
		}
		return response;
	}
}
