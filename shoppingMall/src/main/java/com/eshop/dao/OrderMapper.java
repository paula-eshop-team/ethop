package com.eshop.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.eshop.pojo.Order;

/**
 * 
 * @author Paula Lin
 *
 */
public interface OrderMapper {
    /**
     * @param id
     * @return
     */
    int deleteOrderMapperByPrimaryKey(Integer id);

    /**
     * @param record
     * @return
     */
    int insertOrderMapper(Order record);

    /**
     * @param record
     * @return
     */
    int insertOrderMapperSelective(Order record);

    /**
     * @param id
     * @return
     */
    Order selectOrderMapperByPrimaryKey(Integer id);

    /**
     * @param record
     * @return
     */
    int updateOrderMapperByPrimaryKeySelective(Order record);

    /**
     * @param record
     * @return
     */
    int updateOrderMapperByPrimaryKey(Order record);
    
    /**
     * @param userId
     * @param orderNo
     * @return
     */
    Order selectOrderMapperByUserIdAndOrderNo(@Param("userId")Integer userId,@Param("orderNo")Long orderNo);

    /**
     * @param orderNo
     * @return
     */
    Order selectoOrderMapperByOrderNo(Long orderNo);

    /**
     * @param userId
     * @return
     */
    List<Order> selectOrderMapperByUserId(Integer userId);

    /**
     * @return
     */
    List<Order> selectAllOrderMapper();
}