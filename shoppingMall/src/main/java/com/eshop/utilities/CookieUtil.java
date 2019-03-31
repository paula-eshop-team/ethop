package com.eshop.utilities;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Paula Lin
 *
 */
public class CookieUtil {

	private static Logger logger = LoggerFactory.getLogger(CookieUtil.class);
	private final static String COOKIE_DOMAIN = "suppermmall.com";
	private final static String COOKIE_NAME = "eshop_login_token";
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	//读cookie
	public static String readLoginToken(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if(cookies != null) {
			for(Cookie cookie: cookies) {
				logger.info("Looping cookies from request,  cookieName:" + cookie.getName() + " cookieValue:" + cookie.getValue() + " cookieDomain:" + cookie.getDomain());
				if(StringUtils.equals(cookie.getName(), COOKIE_NAME)) {
					logger.info("return cookie: cookieName:{}, cookieValue:{}", cookie.getName(), cookie.getValue());
					return cookie.getValue();
				}
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param response
	 * @param token
	 */
	public static void writeLoginToken(HttpServletResponse response, String token) {
		Cookie cookie = new Cookie(COOKIE_NAME, token);
		cookie.setDomain(COOKIE_DOMAIN);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		cookie.setMaxAge(60*60*24*365);
		logger.info("add token to response with cookieName:{}, cookieValue:{}, cookieDomian:{}", cookie.getName(), cookie.getValue(), cookie.getDomain());
		response.addCookie(cookie);
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 */
	public static void delLoginToken(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();
		if(cookies != null) {
			for(Cookie cookie: cookies) {
				if(StringUtils.equals(cookie.getName(), COOKIE_NAME)) {
					cookie.setDomain(COOKIE_DOMAIN);
					cookie.setPath("/");
					cookie.setMaxAge(0);
					logger.info("del cookieName:{}, cookieValue:{}", cookie.getName(), cookie.getValue());
					response.addCookie(cookie);
					return;
				}
			}
		}
	} 
}
