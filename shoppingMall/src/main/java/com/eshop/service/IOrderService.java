package com.eshop.service;

import com.eshop.common.ServerResponse;
import com.eshop.vo.OrderVo;
import com.github.pagehelper.PageInfo;

import java.util.Map;

/**
 * 
 * @author Paula Lin
 *
 */
public interface IOrderService {
	/**
	 * 
	 * @param orderNo
	 * @param userId
	 * @param path
	 * @return
	 */
    ServerResponse getQrCodeAddressForPayment(Long orderNo, Integer userId, String path);
    
    /**
     * 
     * @param params
     * @return
     */
    ServerResponse aliCallback(Map<String,String> params);
   
    /**
     * 
     * @param userId
     * @param orderNo
     * @return
     */
    ServerResponse queryOrderPayStatus(Integer userId,Long orderNo);
   
    /**
     * @param userId
     * @param shippingId
     * @return
     */
    ServerResponse createOrder(Integer userId,Integer shippingId);
    
    /**
     * 
     * @param userId
     * @param orderNo
     * @return
     */
    ServerResponse<String> cancelOrder(Integer userId,Long orderNo);
    
    /**
     * 
     * @param userId
     * @return
     */
    ServerResponse getOrderByCartProduct(Integer userId);
    
    /**
     * 
     * @param userId
     * @param orderNo
     * @return
     */
    ServerResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo);
    
    /**
     * 
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    ServerResponse<PageInfo> getOrderList(Integer userId, int pageNum, int pageSize);
    
    /**
     * 
     * @param pageNum
     * @param pageSize
     * @return
     */
    ServerResponse<PageInfo> manageList(int pageNum,int pageSize);
    
    /**
     * 
     * @param orderNo
     * @return
     */
    ServerResponse<OrderVo> manageDetail(Long orderNo);
    
    /**
     * 
     * @param orderNo
     * @param pageNum
     * @param pageSize
     * @return
     */
    ServerResponse<PageInfo> manageSearch(Long orderNo,int pageNum,int pageSize);
    
    /**
     * 
     * @param orderNo
     * @return
     */
    ServerResponse<String> manageSendGoods(Long orderNo);


}
