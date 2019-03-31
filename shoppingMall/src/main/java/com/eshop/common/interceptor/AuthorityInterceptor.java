package com.eshop.common.interceptor;

import com.eshop.common.Const;
import com.eshop.common.ServerResponse;
import com.eshop.pojo.User;
import com.eshop.utilities.CookieUtil;
import com.eshop.utilities.JsonUtil;
import com.eshop.utilities.RedisShardedPoolUtil;
import com.google.common.collect.Maps;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * 
 * @author Paula Lin
 *
 */

public class AuthorityInterceptor implements HandlerInterceptor{

	private static Logger logger = LoggerFactory.getLogger(AuthorityInterceptor.class);
	
	/**
	 * @param httpServletRequest
     * @param httpServletResponse
     * @param handler
     * @return
	 */
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
    	logger.info("preHandle");
        HandlerMethod handlerMethod = (HandlerMethod)handler;
        String methodName = handlerMethod.getMethod().getName();
        String className = handlerMethod.getBean().getClass().getSimpleName();
        if(StringUtils.equals(className,"UserManageController") && StringUtils.equals(methodName,"login")){
        	logger.info("权限拦截器拦截到请求,className:{},methodName:{}",className,methodName);
            return true;
        }

        StringBuffer requestParamBuffer = new StringBuffer();
        Map paramMap = httpServletRequest.getParameterMap();
        Iterator it = paramMap.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry entry = (Map.Entry)it.next();
            String mapKey = (String)entry.getKey();

            String mapValue = StringUtils.EMPTY;

            Object obj = entry.getValue(); 
            if(obj instanceof String[]){
                String[] strs = (String[])obj;
                mapValue = Arrays.toString(strs);
            }
            requestParamBuffer.append(mapKey).append("=").append(mapValue);
        }
        logger.info("权限拦截器拦截到请求,className:{},methodName:{},param:{}",className,methodName,requestParamBuffer.toString());
        User user = null;

        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isNotEmpty(loginToken)){
            String userJsonStr = RedisShardedPoolUtil.get(loginToken);
            user = JsonUtil.string2Obj(userJsonStr,User.class);
        }

        if(user == null || (user.getRole().intValue() != Const.Role.ROLE_ADMIN)){
            httpServletResponse.reset();// 这里要添加reset，否则报异常 getWriter() has already been called for this response.
            httpServletResponse.setCharacterEncoding("UTF-8");//重置response时要设置编码，否则会乱码
            httpServletResponse.setContentType("application/json;charset=UTF-8");//这里要设置返回值的类型，因为全部是json接口。
            PrintWriter out = httpServletResponse.getWriter();

            if(user == null){
                if(StringUtils.equals(className,"ProductManageController") && StringUtils.equals(methodName,"richtextImgUpload")){
                    Map resultMap = Maps.newHashMap();
                    resultMap.put("success",false);
                    resultMap.put("msg","请登录管理员");
                    out.print(JsonUtil.obj2String(resultMap));
                }else{
                    out.print(JsonUtil.obj2String(ServerResponse.createByErrorMessage("拦截器拦截,用户未登录")));
                }
            }else{
                if(StringUtils.equals(className,"ProductManageController") && StringUtils.equals(methodName,"richtextImgUpload")){
                    Map resultMap = Maps.newHashMap();
                    resultMap.put("success",false);
                    resultMap.put("msg","无权限操作");
                    out.print(JsonUtil.obj2String(resultMap));
                }else{
                    out.print(JsonUtil.obj2String(ServerResponse.createByErrorMessage("拦截器拦截,用户无权限操作")));
                }
            }
            out.flush();
            out.close();
            return false;
        }
        return true;
    }

    /**
	 * @param httpServletRequest
     * @param httpServletResponse
     * @param handler
     * @param modelAndView
	 */
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler, ModelAndView modelAndView) throws Exception {
    	logger.info("postHandle");
    }

    /**
	 * @param httpServletRequest
     * @param httpServletResponse
     * @param handler
     * @param ex
	 */
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler, Exception ex) throws Exception {
    	logger.info("afterCompletion");
    }
}
