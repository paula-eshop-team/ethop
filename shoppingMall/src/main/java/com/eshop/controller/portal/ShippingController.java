package com.eshop.controller.portal;

import com.alipay.api.internal.util.StringUtils;
import com.eshop.common.Const;
import com.eshop.common.ResponseCode;
import com.eshop.common.ServerResponse;
import com.eshop.pojo.Shipping;
import com.eshop.pojo.User;
import com.eshop.service.IShippingService;
import com.eshop.utilities.CookieUtil;
import com.eshop.utilities.JsonUtil;
import com.eshop.utilities.RedisPoolUtil;
import com.github.pagehelper.PageInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Description: 
 * This is the controller to handle the request to search or change user's shipping address:
 * 		1.	Add new shipping address.
 *		2.	Delete shipping address.
 *		3.	Update shipping address.
 *		4.	Select shipping address.
 *		5.	List shipping address and paginate it.

 * 
 * @author Paula Lin
 *
 */
@Controller
@RequestMapping("/shipping/")
public class ShippingController {

	private static Logger logger = LoggerFactory.getLogger(ShippingController.class);
	
    @Autowired
    private IShippingService iShippingService;

    /**
     * Description: 
     * This method handler is handle the request to add new shipping address.
     * 
     * @param httpServletRequest
     * @param shipping
     * @return
     */
    @RequestMapping("create_shipping_address.do")
    @ResponseBody
    public ServerResponse createShippingAddress(HttpServletRequest httpServletRequest,Shipping shipping){
    	
    	//Retrieve loginToken from request
    	String loginToken = CookieUtil.readLoginToken(httpServletRequest);
    	
    	//Check if loginToken is empty
		if(StringUtils.isEmpty(loginToken)) {
			return ServerResponse.createByErrorMessage(Const.ErrorMessage.USER_NOT_LOGIN);
		}
	
		//Retrieve json string of user information from Redis according to loginToken
		String userJsonStr = RedisPoolUtil.get(loginToken);
		//Transfer json string of user info to User object.
		User user = JsonUtil.string2Obj(userJsonStr, User.class);		
		
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        
        return iShippingService.addShippingAddress(user.getId(),shipping);
    }

    /**
     * Description: 
     * This method handler is handle the request to delete an existing shipping address.
     * 
     * @param httpServletRequest
     * @param shippingId
     * @return
     */
    @RequestMapping("delete_shipping_address.do")
    @ResponseBody
    public ServerResponse deleteShippingAddress(HttpServletRequest httpServletRequest,Integer shippingId){
    	//Retrieve loginToken from request
    	String loginToken = CookieUtil.readLoginToken(httpServletRequest);
    	
    	//Check if loginToken is empty
    	if(StringUtils.isEmpty(loginToken)) {
			return ServerResponse.createByErrorMessage(Const.ErrorMessage.USER_NOT_LOGIN);
		}
    	//Retrieve json string of user information from Redis according to loginToken
    	String userJsonStr = RedisPoolUtil.get(loginToken);
    	//Transfer json string of user info to User object.
    	User user = JsonUtil.string2Obj(userJsonStr, User.class);		
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.deleteShippingAddress(user.getId(),shippingId);
    }

    /**
     * Description: 
     * This method handler is handle the request to update shipping address.
     * 
     * @param httpServletRequest
     * @param shipping
     * @return
     */
    @RequestMapping("update_shipping_address.do")
    @ResponseBody
    public ServerResponse updateShippingAddress(HttpServletRequest httpServletRequest,Shipping shipping){
    	
    	//Retrieve loginToken from request
    	String loginToken = CookieUtil.readLoginToken(httpServletRequest);
		
    	//Check if loginToken is empty
    	if(StringUtils.isEmpty(loginToken)) {
			return ServerResponse.createByErrorMessage(Const.ErrorMessage.USER_NOT_LOGIN);
		}
		
    	//Retrieve json string of user information from Redis according to loginToken
    	String userJsonStr = RedisPoolUtil.get(loginToken);
    	//Transfer json string of user info to User object.
    	User user = JsonUtil.string2Obj(userJsonStr, User.class);		
		
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.updateShippingAddress(user.getId(),shipping);
    }

    /**
     * Description: 
     * This method handler is handle the request to select shipping address.
     * 
     * @param httpServletRequest
     * @param shippingId
     * @return
     */
    @RequestMapping("select_shipping_address.do")
    @ResponseBody
    public ServerResponse<Shipping> selectShippingAddress(HttpServletRequest httpServletRequest,Integer shippingId){
    	//Retrieve loginToken from request
    	String loginToken = CookieUtil.readLoginToken(httpServletRequest);
    	//Check if loginToken is empty
		if(StringUtils.isEmpty(loginToken)) {
			return ServerResponse.createByErrorMessage(Const.ErrorMessage.USER_NOT_LOGIN);
		}
		
		//Retrieve json string of user information from Redis according to loginToken
		String userJsonStr = RedisPoolUtil.get(loginToken);
		//Transfer json string of user info to User object.
		User user = JsonUtil.string2Obj(userJsonStr, User.class);
		
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.selectShippingAddress(user.getId(),shippingId);
    }

    /**
     * Description: 
     * This method handler is handle the request to list all the shipping address and paginate it.
     * 
     * @param pageNum
     * @param pageSize
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("list_shipping_address.do")
    @ResponseBody
    public ServerResponse<PageInfo> listShippingAddress(@RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10")int pageSize,
                                         HttpServletRequest httpServletRequest){
        
    	//Retrieve loginToken from request
    	String loginToken = CookieUtil.readLoginToken(httpServletRequest);
    	
    	//Check if loginToken is empty
		if(StringUtils.isEmpty(loginToken)) {
			return ServerResponse.createByErrorMessage(Const.ErrorMessage.USER_NOT_LOGIN);
		}
		
		//Retrieve json string of user information from Redis according to loginToken
		String userJsonStr = RedisPoolUtil.get(loginToken);
		//Transfer json string of user info to User object.
		User user = JsonUtil.string2Obj(userJsonStr, User.class);
		
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.listShippingAddress(user.getId(),pageNum,pageSize);
    }














}
