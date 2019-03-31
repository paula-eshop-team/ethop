package com.eshop.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.eshop.pojo.Shipping;

/**
 * 
 * @author Paula Lin
 *
 */
public interface ShippingMapper {
    /**
     * @param id
     * @return
     */
    int deleteShippingByPrimaryKey(Integer id);

    /**
     * @param record
     * @return
     */
    int insertShipping(Shipping record);

    /**
     * @param record
     * @return
     */
    int insertShippingSelective(Shipping record);

    /**
     * @param id
     * @return
     */
    Shipping selectShippingByPrimaryKey(Integer id);

    /**
     * @param record
     * @return
     */
    int updateShippingByPrimaryKeySelective(Shipping record);

    /**
     * @param record
     * @return
     */
    int updateShippingByPrimaryKey(Shipping record);
    
    /**
     * @param userId
     * @param shippingId
     * @return
     */
    int deleteShipingByShippingIdUserId(@Param("userId")Integer userId,@Param("shippingId") Integer shippingId);

    /**
     * @param record
     * @return
     */
    int updateShippingByShipping(Shipping record);

    /**
     * @param userId
     * @param shippingId
     * @return
     */
    Shipping selectShippingByShippingIdUserId(@Param("userId")Integer userId,@Param("shippingId") Integer shippingId);

    /**
     * @param userId
     * @return
     */
    List<Shipping> selectShippingByUserId(@Param("userId")Integer userId);
    
    /**
     * @return
     */
    List<Shipping> findAllShippingListInfo();
}