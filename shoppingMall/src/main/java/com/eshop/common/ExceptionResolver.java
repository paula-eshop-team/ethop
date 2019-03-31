package com.eshop.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import com.eshop.common.interceptor.AuthorityInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author Paula Lin
 *
 */

@Component
public class ExceptionResolver implements HandlerExceptionResolver{
	
	private static Logger logger = LoggerFactory.getLogger(ExceptionResolver.class);
	
	/**
	 * @param httpServletRequest
     * @param httpServletResponse
     * @param object
     * @param ex 
     * @return
	 */
    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object, Exception ex) {
    	//哪一个连接URI发生了Exception
        logger.error("{} Exception",httpServletRequest.getRequestURI(),ex);
        
        //将ModelAndView转化为Json
        //如果使用Jackson2.x, 则要使用MappingJackson2JsonView
        //目前使用的Jackson是1.x, 故使用MappingJacksonJsonView
        //MappingJacksonJsonView是AbstractView的子类, 而AbstractView实现View接口
        ModelAndView modelAndView = new ModelAndView(new MappingJacksonJsonView());

        modelAndView.addObject("status",ResponseCode.ERROR.getCode());
        modelAndView.addObject("msg","接口异常,详情请查看服务端日志的异常信息");
        //e.toString()返回是简单扼要的异常
        modelAndView.addObject("data",ex.toString());
        return modelAndView;
    }

}
