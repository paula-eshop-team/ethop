package com.eshop.controller.portal;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.internal.util.StringUtils;
import com.alipay.demo.trade.config.Configs;
import com.eshop.common.Const;
import com.eshop.common.ResponseCode;
import com.eshop.common.ServerResponse;
import com.eshop.pojo.User;
import com.eshop.service.IOrderService;
import com.eshop.utilities.CookieUtil;
import com.eshop.utilities.JsonUtil;
import com.eshop.utilities.RedisPoolUtil;
import com.google.common.collect.Maps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

/**
 * Description: 
 * This is the controller to handle the request from frontend system and alipay system to create and query order and payment transaction.
 * 		1.	Create new order
 *		2.	Cancel order
 *		3.	Get the previewed order for the selected products in shopping cart.
 *		4.	Get the details of one order.
 *		5.	Get order list  one user.
 *		6.	Get the ftp address of qr code of payment for one order.
 *		7.	Get the callback information from alipay to validate payment transaction for order.
 *		8.	Check the status of payment.

 * @author Paula Lin
 *
 */
@Controller
@RequestMapping("/order/")
public class OrderController {

    private static  final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService iOrderService;

    /**
     * Description: 
     * This method handler is to create new order with shipping address.
     * 
     * @param httpServletRequest
     * @param shippingId
     * @return
     */
    @RequestMapping("create_order.do")
    @ResponseBody
    public ServerResponse createOrder(HttpServletRequest httpServletRequest, Integer shippingId){
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
        return iOrderService.createOrder(user.getId(),shippingId);
    }

    /**
     * Description: 
     * This method handler is to cancel one order.
     * 
     * @param httpServletRequest
     * @param orderNo
     * @return
     */
    @RequestMapping("cancel_order.do")
    @ResponseBody
    public ServerResponse cancelOrder(HttpServletRequest httpServletRequest, Long orderNo){
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
        return iOrderService.cancelOrder(user.getId(),orderNo);
    }


    /**
     * Description: 
     * This method handler is to get the previewed order for the selected products in shopping cart.
     * 
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("get_previewed_order.do")
    @ResponseBody
    public ServerResponse getPreviewedOrder(HttpServletRequest httpServletRequest){
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
        return iOrderService.getOrderByCartProduct(user.getId());
    }


    /**
     * Description: 
     * This method handler is to get the details of one order.
     * 
     * @param httpServletRequest
     * @param orderNo
     * @return
     */
    @RequestMapping("get_order_detail.do")
    @ResponseBody
    public ServerResponse getOrderdetail(HttpServletRequest httpServletRequest,Long orderNo){
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
        return iOrderService.getOrderDetail(user.getId(),orderNo);
    }

    /**
     * Description: 
     * This method handler is to get order list  one user.
     * 
     * @param httpServletRequest
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("get_order_list.do")
    @ResponseBody
    public ServerResponse getOrderList(HttpServletRequest httpServletRequest, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
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
        return iOrderService.getOrderList(user.getId(),pageNum,pageSize);
    }

    /**
     * Description: 
     * This method handler is to get the ftp address of qr code of payment for one order.
     * System return generated qr code of payment and then upload to ftp for further loading in front-end system.
     * Then the buyer/user can scan the qr code for further payment.
     * 
     * @param httpServletRequest
     * @param orderNo
     * @param request
     * @return
     */
    /*
     * 支付功能
     * 为订单号orderNo支付
     */
    @RequestMapping("get_qrCode_addres.do")
    @ResponseBody
    public ServerResponse getQrCodeAddressForPayment(HttpServletRequest httpServletRequest, Long orderNo, HttpServletRequest request){
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
        
        String path = request.getSession().getServletContext().getRealPath("upload");
        return iOrderService.getQrCodeAddressForPayment(orderNo,user.getId(),path);
    }

    /**
     * Description: 
     * This method handler is handle get the callback information from alipay to validate payment transaction for order.
     * 
     * 支付宝的回调函数
     * 
     * @param request, HttpServletRequest from alipay
     * @return
     */
    @RequestMapping("alipay_callback.do")
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request){
    	//Parse the request paramater from alipay and put to Map<String, String> params.
        Map<String,String> params = Maps.newHashMap();
        Map requestParams = request.getParameterMap();
        for(Iterator iter = requestParams.keySet().iterator();iter.hasNext();){
            String name = (String)iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for(int i = 0 ; i <values.length;i++){

                valueStr = (i == values.length -1)?valueStr + values[i]:valueStr + values[i]+",";
            }
            params.put(name,valueStr);
        }
        logger.info("Callback from alipay ,sign:{},trade_status:{}, params:{}",params.get("sign"),params.get("trade_status"),params.toString());

        //Using AlipaySignature.rsaCheckV2() to validate if the payment order is from alipay 
        params.remove("sign_type");
        try {
            boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());
            
            //if alipayRSACheckedV2(return from alipay) is false, that means the payment order is not valid in alipay
            if(!alipayRSACheckedV2){
                return ServerResponse.createByErrorMessage("Invalid request...payment order is not valid in alipay, and will report to the police if receive it again!");
            }
        } catch (AlipayApiException e) {
            logger.error("Exception when callback validation from alipay",e);
        }

        //Validate the payment transaction
        ServerResponse serverResponse = iOrderService.aliCallback(params);
        if(serverResponse.isSuccess()){
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;
    }

    /**
     * Description: 
     * This method handler is to check the status of payment, check if the payment of product order has been completed.
     *
     * 买家扫码付款成功后,前台会调用该接口,查看是否付款成功
     * 
     * @param httpServletRequest
     * @param orderNo
     * @return
     */
    @RequestMapping("query_order_pay_status.do")
    @ResponseBody
    public ServerResponse<Boolean> queryOrderPayStatus(HttpServletRequest httpServletRequest, Long orderNo){
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

        ServerResponse serverResponse = iOrderService.queryOrderPayStatus(user.getId(),orderNo);
        if(serverResponse.isSuccess()){
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);
    }
    
}
