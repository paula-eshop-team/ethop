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
	///该方法在controller处理之前被调用
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
    	logger.info("preHandle");
        //请求中Controller中的方法名
        HandlerMethod handlerMethod = (HandlerMethod)handler;

        //解析HandlerMethod
        
        //handlerMethod.getMethod()获取Controller下处理请求对应的方法的方法对象
        String methodName = handlerMethod.getMethod().getName();
        //handlerMethod.getBean()获取Controller对象, 改Controller对象注入了对应的Service和Mapper
        String className = handlerMethod.getBean().getClass().getSimpleName();

        //处理拦截到UserManageController中login.do请求, 不进行拦截器判断处理, 因为还没登录, user信息在redis中就没有缓存
        if(StringUtils.equals(className,"UserManageController") && StringUtils.equals(methodName,"login")){
        	logger.info("权限拦截器拦截到请求,className:{},methodName:{}",className,methodName);
            //如果是拦截到登录请求，不打印参数，因为参数里面有密码，全部会打印到日志中，防止日志泄露
            return true;
        }
        
        //解析传入Controller下方法的方法参数,具体的参数key以及value是什么，我们打印日志
        StringBuffer requestParamBuffer = new StringBuffer();
        Map paramMap = httpServletRequest.getParameterMap();
        Iterator it = paramMap.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry entry = (Map.Entry)it.next();
            String mapKey = (String)entry.getKey();//entry.getKey()返回值是泛型K

            String mapValue = StringUtils.EMPTY;

            //request这个参数的map，里面的value返回的是一个String[]
            Object obj = entry.getValue(); //entry.getValue()返回值是泛型V
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
            //返回false.即不会调用controller里的方法
        	
        	//拦截器中, 需要response将输出进行重新的时候, 一定要调用response.reset(), 将response进行重置
            httpServletResponse.reset();// 这里要添加reset，否则报异常 getWriter() has already been called for this response.
            httpServletResponse.setCharacterEncoding("UTF-8");//重置response时要设置编码，否则会乱码
            httpServletResponse.setContentType("application/json;charset=UTF-8");//这里要设置返回值的类型，因为全部是json接口。

            PrintWriter out = httpServletResponse.getWriter();

            //上传由于富文本的控件要求，要特殊处理返回值，这里面区分是否登录以及是否有权限
            if(user == null){
            	//返回false, 即不会调用controller里的方法
            	//拦截器preHandle, postHandle, afterCompletion的返回值是boolean, 而请求的返回响应是ServerResponse, 富文本还返回Map对象,
            	//最终都要转为json, 通过HttpServletResponse的getWriter进行输出
            	
            	//if为处理产品富文本上传, 拦截ProductManageController中的richtextImgUpload.do的时候
            	//返回实体是Map resultMap, 最后转化为json, 通过HttpServletResponse的getWriter进行输出
                if(StringUtils.equals(className,"ProductManageController") && StringUtils.equals(methodName,"richtextImgUpload")){
                    Map resultMap = Maps.newHashMap();
                    resultMap.put("success",false);
                    resultMap.put("msg","请登录管理员");
                    out.print(JsonUtil.obj2String(resultMap));
                }else{
                	//返回结果为json数据, 如下:
                	//{
                	//	"status": 1,
                	//	"msg" : "拦截器拦截,用户未登录"
                	//}
                    out.print(JsonUtil.obj2String(ServerResponse.createByErrorMessage("拦截器拦截,用户未登录")));
                }
            }else{
            	//if为处理产品富文本上传, 拦截ProductManageController中的richtextImgUpload.do的时候
            	//返回实体是Map resultMap, 最后转化为json, 通过HttpServletResponse的getWriter进行输出
                if(StringUtils.equals(className,"ProductManageController") && StringUtils.equals(methodName,"richtextImgUpload")){
                    Map resultMap = Maps.newHashMap();
                    resultMap.put("success",false);
                    resultMap.put("msg","无权限操作");
                    out.print(JsonUtil.obj2String(resultMap));
                }else{
                    out.print(JsonUtil.obj2String(ServerResponse.createByErrorMessage("拦截器拦截,用户无权限操作")));
                }
            }
            out.flush();//清空out流中的数据
            out.close();// 这里要关闭

            //返回false, 表示不需要进入Controller
            return false;
        }
        
        //信息正常, 可以进入Controller
        return true;
    }

    /**
	 * @param httpServletRequest
     * @param httpServletResponse
     * @param handler
     * @param modelAndView
	 */
    //该方法在controller处理之后被调用
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
    //在所有执行之后被调用, 如果系统不是前后端分离, 那么就砸视图呈现之后调用afterCompletion
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler, Exception ex) throws Exception {
    	logger.info("afterCompletion");
    }
}
