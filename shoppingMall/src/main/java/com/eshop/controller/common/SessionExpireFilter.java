package com.eshop.controller.common;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eshop.common.Const;
import com.eshop.pojo.User;
import com.eshop.utilities.CookieUtil;
import com.eshop.utilities.JsonUtil;
import com.eshop.utilities.RedisPoolUtil;

/**
 * 
 * @author Paula Lin
 *
 */
public class SessionExpireFilter implements Filter {

	private static Logger logger = LoggerFactory.getLogger(SessionExpireFilter.class);
	
	/**
	 * @param servletRequest
	 * @param servletResponse
	 * @param filterChain
	 */
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		//将ServletRequest强转为HttpServletRequest
		HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
		
		String loginToken = CookieUtil.readLoginToken(httpServletRequest);
		
		//判断logintoken是否为空或者为空字符串"";
		if(StringUtils.isNotEmpty(loginToken)) {
			//如果不为空,符合条件, 继续拿着user信息
			String userJsonStr = RedisPoolUtil.get(loginToken);
			User user = JsonUtil.string2Obj(userJsonStr, User.class);
			
			if(user != null) {
				//如果user不为空, 则重置session的时间为30分钟, 即调用expire命令
				RedisPoolUtil.expire(loginToken, Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
			}
		}
		
		filterChain.doFilter(servletRequest, servletResponse);
	}

}
