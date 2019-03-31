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
	
	//".eshop.com"是一级域名,二级域名及以上可以读到该cookie..... "xxx.eshop.com"是二级域名
	private final static String COOKIE_DOMAIN = "suppermmall.com";
	//COOKIE_NAME是服务端要种到客户端浏览器上的
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
				//org.apache.commons.lang.StringUtils类中的equals方法已经对cookie.getName()做空值(null)判断, 避免空指针异常
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
	//用户登录时写入cookie
	public static void writeLoginToken(HttpServletResponse response, String token) {
		//cookie种在.eshop.com,那么www.eshop.com和user.eshop.com都可以获取到相同的cookie
		Cookie cookie = new Cookie(COOKIE_NAME, token);
		cookie.setDomain(COOKIE_DOMAIN);
		//path为"/"表示路径为根目录,, 如果path为"test", 那么只有"test"目录及其子目录下的页面和代码才能获取到cookie
		cookie.setPath("/");
		//为了防止脚本攻击带来的信息泄露风险, httpOnly属性规定, 不许通过脚本访问cookie, 使用该属性后, web站点就能够排除cookie中的敏感信息被发送给黑客计算机或者使用脚本的web站点的可能性
		//httpOnly设置为true后, 是无法通过脚本来获取cookie信息的, 同时浏览器也不会把cookie发送给任何第三方, 这样就保证了信息的安全; 当然, 其无法全面保障站点攻击的安全, 但能够提供一定的安全性
		//tomcat7, 默认用servlet3.0, 故httpOnly属性可以直接加上; 如果是tomcat6, servlet2.x并不提供该属性的set方法
		cookie.setHttpOnly(true);
		//maxAge是设置cookie的有效期, 单位是秒,  -1代表永久...如果maxAge不设置, 则cookie不会写到硬盘, 只会写到内存, 只在客户端当前页面有效.
		//如下, 将cookie设置为1年的有效期, 60*60*24*365
		cookie.setMaxAge(60*60*24*365);
		logger.info("add token to response with cookieName:{}, cookieValue:{}, cookieDomian:{}", cookie.getName(), cookie.getValue(), cookie.getDomain());
		
		//将cookie返回给客户端浏览器
		response.addCookie(cookie);
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 */
	//删除cookie
	public static void delLoginToken(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();
		if(cookies != null) {
			for(Cookie cookie: cookies) {
				if(StringUtils.equals(cookie.getName(), COOKIE_NAME)) {
					cookie.setDomain(COOKIE_DOMAIN);
					cookie.setPath("/");
					//maxAge有效期设置为0, 代表删除此cookie
					cookie.setMaxAge(0);
					logger.info("del cookieName:{}, cookieValue:{}", cookie.getName(), cookie.getValue());
					response.addCookie(cookie);
					return;
				}
			}
		}
	} 
}
