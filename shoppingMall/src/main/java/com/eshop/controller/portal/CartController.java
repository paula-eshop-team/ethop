package com.eshop.controller.portal;

import com.alipay.api.internal.util.StringUtils;
import com.eshop.common.Const;
import com.eshop.common.ResponseCode;
import com.eshop.common.ServerResponse;
import com.eshop.pojo.User;
import com.eshop.service.ICartService;
import com.eshop.utilities.CookieUtil;
import com.eshop.utilities.JsonUtil;
import com.eshop.utilities.RedisPoolUtil;
import com.eshop.vo.CartVo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Description: 
 * This is the controller to handle the request to query or update the shopping cart:
 * 		1.	Get the list for shopping cart, including product, unit price, purchase volume and total price of products.
 *		2.	Update purchase volume of one product in shopping cart.
 *		3.	Delete one product in shopping cart.
 *		4.	Mark one or all the products in shopping cart as selected status or unselected status
 *		5.	Get the quantity of products in shopping cart which is used to show in the upper-right corner of the eshop page.
 *		    For example,  purchase volume of product A is 10, purchase volume of product B is 10, 
 *          then the quantity of products for the shopping cart is 30.
 * 
 * @author Paula Lin
 *
 */
@Controller
@RequestMapping("/cart/")
public class CartController {

	private static Logger logger = LoggerFactory.getLogger(CartController.class);
    @Autowired
    private ICartService iCartService;

    /**
     * Description: 
     * This method handler is to get the list for shopping cart, including product, unit price, purchase volume and total price of products.
     * 查询购物车
     * 
     * @param httpServletRequest
     * @return
     */
    
    @RequestMapping("list_Cart.do")
    @ResponseBody
    public ServerResponse<CartVo> listCart(HttpServletRequest httpServletRequest){
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
        
        return iCartService.listCart(user.getId());
    }

    /**
     * Description: 
     * This method handler is to add one product and  purchase volume to shopping cart.
     * 添加商品到购物车
     * 
     * @param httpServletRequest
     * @param count
     * @param productId
     * @return
     */
    @RequestMapping("add_product.do")
    @ResponseBody
    public ServerResponse<CartVo> addProduct(HttpServletRequest httpServletRequest, Integer count, Integer productId){
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
        return iCartService.addProduct(user.getId(),productId,count);
    }

    /**
     * Description: 
     * This method handler is to update purchase volume of one product in shopping cart.
     * 更新购物车,即更新购物车中商品的的数量
     * 
     * @param httpServletRequest
     * @param count
     * @param productId
     * @return
     */
    @RequestMapping("update_product.do")
    @ResponseBody
    ServerResponse<CartVo> updateProduct(HttpServletRequest httpServletRequest, Integer count, Integer productId){
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
        return iCartService.updateProduct(user.getId(),productId,count);
    }

    /**
     * Description: 
     * This method handler is to delete one product in shopping cart.
     * 删除购物车中的商品
     * 
     * @param httpServletRequest
     * @param productIds
     * @return
     */
    @RequestMapping("delete_product.do")
    @ResponseBody
    public ServerResponse<CartVo> deleteProduct(HttpServletRequest httpServletRequest,String productIds){
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
        return iCartService.deleteProduct(user.getId(),productIds);
    }

    /**
     * Description: 
     * This method handler is to mark all the products in shopping cart as selected status.
     * 全选购物车中的所有商品
     * 
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("select_all_products.do")
    @ResponseBody
    public ServerResponse<CartVo> selectAllProducts(HttpServletRequest httpServletRequest){
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
        return iCartService.selectOrUnSelectProduct(user.getId(),null,Const.Cart.CHECKED);
    }

    /**
     * Description: 
     * This method handler is to mark all the products in shopping cart as unselected status.
     * 取消勾选购物车中的所有商品
     * 
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("un_select_all_products.do")
    @ResponseBody
    public ServerResponse<CartVo> unSelectAllProducts(HttpServletRequest httpServletRequest){
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
        return iCartService.selectOrUnSelectProduct(user.getId(),null,Const.Cart.UN_CHECKED);
    }

    /**
     * Description: 
     * This method handler is to mark one the products in shopping cart as selected status.
     * 单独选购物车中的某个商品
     * 
     * @param httpServletRequest
     * @param productId
     * @return
     */
    @RequestMapping("select_one_product.do")
    @ResponseBody
    public ServerResponse<CartVo> selectOneProduct(HttpServletRequest httpServletRequest,Integer productId){
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
        return iCartService.selectOrUnSelectProduct(user.getId(),productId,Const.Cart.CHECKED);
    }

    /**
     * Description: 
     * This method handler is to mark one the products in shopping cart as selected status.
     * 
     * 单独反选购物车中某个商品
     * 
     * @param httpServletRequest
     * @param productId
     * @return
     */
    @RequestMapping("un_select_one_product.do")
    @ResponseBody
    ServerResponse<CartVo> unSelectOneProduct(HttpServletRequest httpServletRequest,Integer productId){
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
        return iCartService.selectOrUnSelectProduct(user.getId(),productId,Const.Cart.UN_CHECKED);
    }

    /**
     * Description: 
     * This method handler is to get the count of products in shopping cart which is used to show in the upper-right corner of the eshop page.
     * For example,  purchase volume of product A is 10, purchase volume of product B is 10, then the count of products for the shopping cart is 30.
     * 查询当前用户的购物车里面的产品数量,用于显示在页面右上角的购物车上. 例如产品a有10个,产品b有20个,那么购物车数量就是30.
     * 
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("get_cart_product_count.do")
    @ResponseBody
    public ServerResponse<Integer> getCartProductCount(HttpServletRequest httpServletRequest){
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
            return ServerResponse.createBySuccess(0);
        }
        return iCartService.getCartProductCount(user.getId());
    }

}
