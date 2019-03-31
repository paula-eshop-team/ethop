package com.eshop.service;

import java.util.List;

import com.eshop.common.ServerResponse;
import com.eshop.pojo.Shipping;
import com.github.pagehelper.PageInfo;

/**
 * 
 * @author Paula Lin
 *
 */
public interface IShippingService {
	/**
	 * 
	 * @param userId
	 * @param shipping
	 * @return
	 */
    ServerResponse addShippingAddress(Integer userId, Shipping shipping);
    
    /**
     * 
     * @param userId
     * @param shippingId
     * @return
     */
    ServerResponse<String> deleteShippingAddress(Integer userId,Integer shippingId);
    
    /**
     * 
     * @param userId
     * @param shipping
     * @return
     */
    ServerResponse updateShippingAddress(Integer userId, Shipping shipping);
    
    /**
     * 
     * @param userId
     * @param shippingId
     * @return
     */
    ServerResponse<Shipping> selectShippingAddress(Integer userId, Integer shippingId);
    
    /**
     * 
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    ServerResponse<PageInfo> listShippingAddress(Integer userId, int pageNum, int pageSize);
    
    List<Shipping> findAllShippingListInfo();

}
